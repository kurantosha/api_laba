package petstoreTests;

import static core.EndpointsPet.PET;
import static core.EndpointsPet.PET_BY_ID;
import static core.EndpointsPet.PET_FIND_BY_STATUS;
import static java.util.Arrays.asList;

import core.models.pet.DeletePetModel;
import core.models.pet.FindByStatusPetModel;
import core.models.pet.NotFoundPetModel;
import core.models.pet.PetModel;
import core.models.pet.PetModel.Category;
import core.models.pet.PetModel.Tags;
import core.models.pet.UpdatePetModel;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

public class PetsTest extends BaseTest {

  static String petId;
  static String petName = "Rex";
  Category PetCategory = new Category(10, "Dogs");
  List<String> photoUrls = new ArrayList<>(asList("https://unsplash.com/photos/v3-zcCWMjgM",
      "https://unsplash.com/photos/T-0EW-SEbsE", "https://unsplash.com/photos/BJaqPaH6AGQ"));
  List<Tags> tagsList = new ArrayList<>(
      asList(new Tags(31, "Small dog"), new Tags(30, "Cute"), new Tags(20, "Silent")));

  // Task 1
  @Test
  public void checkThatCreatedNewPetTest() {
    // Request
    PetModel expectPetModel = PetModel.builder()
        .name(petName)
        .category(PetCategory)
        .tags(tagsList)
        .photoUrls(photoUrls)
        .status("available")
        .build();

    // Response
    ValidatableResponse petResponse = RestAssured
        .given()
        .body(expectPetModel)
        .when()
        .post(PET)
        .then()
        .statusCode(200);

    PetModel actualPetModel = petResponse.extract().as(PetModel.class);
    petId = actualPetModel.getId(); // get ID from created pet

    // Assertions
    SoftAssertions softAssertions = new SoftAssertions();
    // Check that after creation pet id not equals 0
    softAssertions.assertThat(petId)
        .as("Pet id equals 0, after creation")
        .isNotEqualTo(0);

    // Check that all data in response the same as in request
    softAssertions.assertThat(actualPetModel)
        .as("We are waited pet after created: [" + expectPetModel
            + "] but  and received: [" + actualPetModel + "]")
        .isEqualToIgnoringGivenFields(expectPetModel, "id");

    softAssertions.assertAll();
  }

  // Task2
  @Test(dependsOnMethods = "checkThatCreatedNewPetTest")
  public void checkThatGetPetByPetIdTest() {
    // Response
    ValidatableResponse petResponse = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .get(PET_BY_ID)
        .then()
        .statusCode(200);

    PetModel actualPetModel = petResponse.extract().as(PetModel.class);

    // Assertions
    SoftAssertions softAssertions = new SoftAssertions();

    // Check that pet name in response  -  Rex
    softAssertions.assertThat(actualPetModel.getName())
        .as("We are waited name pet after created: [" + petName + "] but  and received: ["
            + actualPetModel.getName() + "]")
        .isEqualTo(petName);

    // Check that pet status  - available
    softAssertions.assertThat(actualPetModel.getStatus())
        .as("We are waited status pet after created: [available] but  and received: ["
            + actualPetModel.getStatus() + "]")
        .isEqualTo("available");

    softAssertions.assertAll();
  }

  // Task3
  @Test(dependsOnMethods = "checkThatCreatedNewPetTest")
  public void checkThatUpdatePetNameAndStatusTest() {

    String updateNamePet = "Sky";
    String updateStatusPet = "sold";

    ValidatableResponse updatePetResponse = RestAssured
        .given()
        .contentType("application/x-www-form-urlencoded")
        .pathParam("id", petId)
        .formParam("name", updateNamePet)
        .formParam("status", updateStatusPet)
        .when()
        .post(PET_BY_ID)
        .then()
        .statusCode(200);

    UpdatePetModel actualUpdatePetModel = updatePetResponse.extract().as(UpdatePetModel.class);

    SoftAssertions softAssertions = new SoftAssertions();

    softAssertions.assertThat(actualUpdatePetModel.getMessage())
        .as("Value of message field in response after update pet not equals to pet Id")
        .isEqualTo(petId);

    ValidatableResponse petAfterUpdateResponse = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .get(PET_BY_ID)
        .then()
        .statusCode(200);

    PetModel actualAfterUpdatePetMode = petAfterUpdateResponse.extract().as(PetModel.class);

    // Check that name was saved
    softAssertions.assertThat(actualAfterUpdatePetMode.getName())
        .as("Name pet was not saved")
        .isEqualTo(updateNamePet);

    // Check that status was saved
    softAssertions.assertThat(actualAfterUpdatePetMode.getStatus())
        .as("Status pet was not saved")
        .isEqualTo(updateStatusPet);

    softAssertions.assertAll();
  }

  // Task 4
  @Test(dependsOnMethods = "checkThatCreatedNewPetTest")
  public void checkThatPetIsDeletedTest() {
    // Response
    ValidatableResponse petResponse = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .delete(PET_BY_ID)
        .then()
        .statusCode(200);

    DeletePetModel deleteResponse = petResponse.extract().as(DeletePetModel.class);

    // Assertions
    SoftAssertions softAssertions = new SoftAssertions();
    // Check that value of message field in response equals to pet ID
    softAssertions.assertThat(deleteResponse.getMessage())
        .as("Value of message field in response not equals to pet ID")
        .isEqualTo(String.valueOf(petId));

    ValidatableResponse petResponseAfterDelete = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .get(PET_BY_ID)
        .then()
        .statusCode(404);

    NotFoundPetModel actualPetModelAfterDelete = petResponseAfterDelete.extract()
        .as(NotFoundPetModel.class);
    softAssertions.assertThat(actualPetModelAfterDelete.getCode())
        .as("Code in body not equals to: [1]")
        .isEqualTo(1);

    softAssertions.assertThat(actualPetModelAfterDelete.getType())
        .as("Type in body not equals to: [error]")
        .isEqualTo("error");

    softAssertions.assertThat(actualPetModelAfterDelete.getMessage())
        .as("Message in body not equals to: [Pet not found]")
        .isEqualTo("Pet not found");

    softAssertions.assertAll();
  }

  // Task 5
  @Test
  public void checkThatFindsPetByStatusTest() {

    String myPetName = "Baddy";
    Category myPetCategory = new Category(16, "Dogs");
    List<String> photosUrl = new ArrayList<>(asList("https://unsplash.com/photos/v3-zcCWMjgM",
        "https://unsplash.com/photos/T-0EW-SEbsE", "https://unsplash.com/photos/BJaqPaH6AGQ"));
    List<Tags> myPetTagsList = new ArrayList<>(
        asList(new Tags(30, "Big dog"), new Tags(29, "Dalmatian"), new Tags(20, "WhiteAndBlack")));
    String myPetStatus = "sold";

    PetModel expectPetModel = PetModel.builder()
        .name(myPetName)
        .category(myPetCategory)
        .tags(myPetTagsList)
        .photoUrls(photosUrl)
        .status(myPetStatus)
        .build();

    // Creating Response
    ValidatableResponse creatingPetResponse = RestAssured
        .given()
        .body(expectPetModel)
        .when()
        .post(PET)
        .then()
        .statusCode(200);

    // Find Status Response
    ValidatableResponse petResponse = RestAssured
        .given()
        .queryParam("status", myPetStatus)
        .when()
        .get(PET_FIND_BY_STATUS)
        .then()
        .statusCode(200);

    List<FindByStatusPetModel> findByStatusResponse = Arrays.asList(
        petResponse.extract().as(FindByStatusPetModel[].class));
    List<String> namesPets = FindByStatusPetModel.getNamesPets(findByStatusResponse);

    // Assertions
    SoftAssertions softAssertions = new SoftAssertions();

    // Check that created dog exist in result body
    softAssertions.assertThat(namesPets)
        .as("Our created dog not exist in result body")
        .contains(myPetName);

    softAssertions.assertAll();
  }
}