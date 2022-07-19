package de.unistuttgart.moorhuhnbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.moorhuhnbackend.data.Configuration;
import de.unistuttgart.moorhuhnbackend.data.ConfigurationDTO;
import de.unistuttgart.moorhuhnbackend.data.Question;
import de.unistuttgart.moorhuhnbackend.data.QuestionDTO;
import de.unistuttgart.moorhuhnbackend.data.mapper.ConfigurationMapper;
import de.unistuttgart.moorhuhnbackend.data.mapper.QuestionMapper;
import de.unistuttgart.moorhuhnbackend.repositories.ConfigurationRepository;
import de.unistuttgart.moorhuhnbackend.repositories.QuestionRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest
class ConfigControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ConfigurationMapper configurationMapper;

  @Autowired
  private QuestionMapper questionMapper;

  @Autowired
  private ConfigurationRepository configurationRepository;

  @Autowired
  private QuestionRepository questionRepository;

  private final String API_URL = "/api/v1/minigames/moorhuhn/configurations";
  private ObjectMapper objectMapper;
  private Configuration createdConfiguration;
  private ConfigurationDTO createdConfigurationDTO;

  @BeforeEach
  public void createBasicData() {
    configurationRepository.deleteAll();
    final Question questionOne = new Question();
    questionOne.setText("Are you cool?");
    questionOne.setRightAnswer("Yes");
    questionOne.setWrongAnswers(Set.of("No", "Maybe"));

    final Question questionTwo = new Question();
    questionTwo.setText("Is this game cool?");
    questionTwo.setRightAnswer("Yes");
    questionTwo.setWrongAnswers(Set.of("No", "Maybe"));

    final Configuration configuration = new Configuration();
    configuration.setQuestions(Set.of(questionOne, questionTwo));

    createdConfiguration = configurationRepository.save(configuration);
    createdConfigurationDTO = configurationMapper.configurationToConfigurationDTO(createdConfiguration);

    objectMapper = new ObjectMapper();
  }

  @AfterEach
  public void deleteBasicData() {
    configurationRepository.deleteAll();
  }

  @Test
  public void getConfigurations() throws Exception {
    final MvcResult result = mvc
      .perform(get(API_URL).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final List<ConfigurationDTO> configurations = Arrays.asList(
      objectMapper.readValue(content, ConfigurationDTO[].class)
    );

    assertSame(1, configurations.size());
    final ConfigurationDTO singleConfiguration = configurations.get(0);
    assertTrue(createdConfigurationDTO.equalsContent(singleConfiguration));
  }

  @Test
  public void getSpecificConfiguration_DoesNotExist_ThrowsNotFound() throws Exception {
    mvc
      .perform(get(API_URL + "/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  @Test
  public void createConfiguration() throws Exception {
    final ConfigurationDTO newCreatedConfigurationDTO = new ConfigurationDTO(
      Set.of(new QuestionDTO("Is this a new configuration?", "Yes", Set.of("Maybe", "No")))
    );
    final String bodyValue = objectMapper.writeValueAsString(newCreatedConfigurationDTO);
    final MvcResult result = mvc
      .perform(post(API_URL).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final ConfigurationDTO newCreatedConfigurationDTOResponse = objectMapper.readValue(content, ConfigurationDTO.class);

    assertNotNull(newCreatedConfigurationDTOResponse.getId());
    // because question object are not equals, we have to compare the content without id
    assertSame(
      newCreatedConfigurationDTO.getQuestions().size(),
      newCreatedConfigurationDTOResponse.getQuestions().size()
    );
    for (QuestionDTO question : newCreatedConfigurationDTO.getQuestions()) {
      assertTrue(
        newCreatedConfigurationDTOResponse
          .getQuestions()
          .stream()
          .anyMatch(filteredQuestion -> question.equalsContent(filteredQuestion))
      );
    }
    assertSame(2, configurationRepository.findAll().size());
  }

  @Test
  public void updateConfiguration() throws Exception {
    final Set<QuestionDTO> newQuestionsDTO = Set.of(
      new QuestionDTO("Is this a new configuration?", "Yes", Set.of("Maybe", "No"))
    );
    createdConfigurationDTO.setQuestions(newQuestionsDTO);
    final String bodyValue = objectMapper.writeValueAsString(createdConfigurationDTO);
    final MvcResult result = mvc
      .perform(
        put(API_URL + "/" + createdConfiguration.getId()).content(bodyValue).contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final ConfigurationDTO updatedConfigurationDTOResponse = objectMapper.readValue(content, ConfigurationDTO.class);

    // because question object are not equals, we have to compare the content without id
    assertSame(createdConfigurationDTO.getQuestions().size(), updatedConfigurationDTOResponse.getQuestions().size());
    for (QuestionDTO question : createdConfigurationDTO.getQuestions()) {
      assertTrue(
        updatedConfigurationDTOResponse
          .getQuestions()
          .stream()
          .anyMatch(filteredQuestion -> question.equalsContent(filteredQuestion))
      );
    }
    assertEquals(createdConfigurationDTO.getId(), updatedConfigurationDTOResponse.getId());
    assertSame(1, configurationRepository.findAll().size());
  }

  @Test
  public void deleteConfiguration() throws Exception {
    final MvcResult result = mvc
      .perform(delete(API_URL + "/" + createdConfiguration.getId()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final ConfigurationDTO deletedConfigurationDTOResponse = objectMapper.readValue(content, ConfigurationDTO.class);

    assertEquals(createdConfigurationDTO.getId(), deletedConfigurationDTOResponse.getId());
    assertTrue(createdConfigurationDTO.equalsContent(deletedConfigurationDTOResponse));
    assertSame(0, configurationRepository.findAll().size());
    createdConfiguration
      .getQuestions()
      .forEach(question -> assertFalse(questionRepository.existsById(question.getId())));
  }

  @Test
  public void addQuestionToExistingConfiguration() throws Exception {
    final QuestionDTO addedQuestionDTO = new QuestionDTO(
      "What is this question about?",
      "Question",
      Set.of("Nothing", "Everything")
    );

    final String bodyValue = objectMapper.writeValueAsString(addedQuestionDTO);
    final MvcResult result = mvc
      .perform(
        post(API_URL + "/" + createdConfiguration.getId() + "/questions")
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final QuestionDTO newAddedQuestionResponse = objectMapper.readValue(content, QuestionDTO.class);

    assertTrue(addedQuestionDTO.equalsContent(newAddedQuestionResponse));
  }

  @Test
  public void removeQuestionFromExistingConfiguration() throws Exception {
    final QuestionDTO removedQuestionDTO = createdConfigurationDTO.getQuestions().stream().findFirst().get();
    assertTrue(questionRepository.existsById(removedQuestionDTO.getId()));

    final MvcResult result = mvc
      .perform(
        delete(API_URL + "/" + createdConfiguration.getId() + "/questions/" + removedQuestionDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final QuestionDTO removedQuestionDTOResult = objectMapper.readValue(content, QuestionDTO.class);

    assertEquals(removedQuestionDTO.getId(), removedQuestionDTOResult.getId());
    assertTrue(removedQuestionDTO.equalsContent(removedQuestionDTOResult));
    assertSame(
      createdConfiguration.getQuestions().size() - 1,
      configurationRepository.findById(createdConfiguration.getId()).get().getQuestions().size()
    );
    assertFalse(questionRepository.existsById(removedQuestionDTO.getId()));
  }

  @Test
  public void updateQuestionFromExistingConfiguration() throws Exception {
    final Question updatedQuestion = createdConfiguration.getQuestions().stream().findFirst().get();
    final QuestionDTO updatedQuestionDTO = questionMapper.questionToQuestionDTO(updatedQuestion);
    final String newText = "Is this a new updated question?";
    updatedQuestionDTO.setText(newText);

    final String bodyValue = objectMapper.writeValueAsString(updatedQuestionDTO);
    final MvcResult result = mvc
      .perform(
        put(API_URL + "/" + createdConfiguration.getId() + "/questions/" + updatedQuestion.getId())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final QuestionDTO updatedQuestionResultDTO = objectMapper.readValue(content, QuestionDTO.class);

    assertTrue(updatedQuestionDTO.equalsContent(updatedQuestionResultDTO));
    assertEquals(newText, updatedQuestionResultDTO.getText());
    assertEquals(newText, questionRepository.findById(updatedQuestion.getId()).get().getText());
  }
}