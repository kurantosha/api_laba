package petstoreTests;

import static core.Endpoints.PET;
import static core.Endpoints.PET_BY_ID;
import static java.util.Arrays.asList;

import core.model.PetModel;
import core.model.PetModel.Category;
import core.model.PetModel.Tags;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import java.util.ArrayList;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

public class PetsTest extends BaseTest {

  static Long petId;
  static String petName = "Rex";

  @Test
  public void checkThatCreatedNewPetTest() {
    // Request
    PetModel expectPetModel = PetModel.builder()
        .name(petName)
        .category(new Category(10, "Dogs"))
        .tags(new ArrayList<>(
            asList(new Tags(31, "Small dog"), new Tags(30, "Cute"), new Tags(20, "Silent"))))
        .photoUrls(new ArrayList<>(asList("https://unsplash.com/photos/v3-zcCWMjgM",
            "https://unsplash.com/photos/T-0EW-SEbsE", "https://unsplash.com/photos/BJaqPaH6AGQ")))
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

    // Check name pet after created
    softAssertions.assertThat(actualPetModel.getName())
        .as("We are waited name pet after created: [" + expectPetModel.getName()
            + "] but  and received: [" + actualPetModel.getName() + "]")
        .isEqualTo(expectPetModel.getName());

    // Check name category of pet after created
    softAssertions.assertThat(actualPetModel.getCategory().getName())
        .as("We are waited status pet after created: [" + expectPetModel.getStatus()
            + "] but  and received: [" + actualPetModel.getStatus() + "]")
        .isEqualTo(expectPetModel.getCategory().getName());

    // Check name tags of pet after created
    softAssertions.assertThat(PetModel.getTagsName(actualPetModel))
        .as("We are waited names from tags of pet after created: [Small dog], [Cute], [Silent] but  and received: ["
            + PetModel.getTagsName(actualPetModel) + "]")
        .contains("Small dog", "Cute", "Silent");

    // Check status pet after created
    softAssertions.assertThat(actualPetModel.getStatus())
        .as("We are waited status pet after created: [" + expectPetModel.getStatus()
            + "] but  and received: [" + actualPetModel.getStatus() + "]")
        .isEqualTo(expectPetModel.getStatus());

    softAssertions.assertAll();
  }


  @Test
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


  //  @Test
  public void checkThatUpdatePetNameAndStatusTest() {

    ValidatableResponse petResponse = RestAssured
        .given()
        .pathParam("id", 9223372016900065963L)
        .formParam("name", asList("Sky4"))
//        .formParams(new HashMap<String, String> (){{
//      put("name", "Sky");
//      put("status", "sold");}})
        .when()
        .post(PET_BY_ID)
        .then()
        .statusCode(200);

    PetModel actualPetModel = petResponse.extract().as(PetModel.class);

    ValidatableResponse petAfterUpdateResponse = RestAssured
        .given()
        .pathParam("id", 9223372016900065963L)
        .when()
        .get(PET_BY_ID)
        .then()
        .statusCode(200);

    PetModel actualPetModel1 = petAfterUpdateResponse.extract().as(PetModel.class);

  }


  @Test
  public void checkThatPetIsDeletedTest() {
    // Response
    ValidatableResponse petResponse = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .delete(PET_BY_ID)
        .then()
        .statusCode(200);

    PetModel actualPetModel = petResponse.extract().as(PetModel.class);

    // Assertions
    SoftAssertions softAssertions = new SoftAssertions();
    // Check that value of message field in response equals to pet Id
    softAssertions.assertThat(actualPetModel.getMessage())
        .as("Value of message field in response not equals to pet ID")
        .isEqualTo(String.valueOf(petId));

    ValidatableResponse petResponseAfterDelete = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .get(PET_BY_ID)
        .then()
        .statusCode(404);

    PetModel actualPetModelAfterDelete = petResponseAfterDelete.extract().as(PetModel.class);
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

}