package petstoreTests;

import static core.EndpointsUser.USER;
import static core.EndpointsUser.USER_BY_USERNAME;
import static core.EndpointsUser.USER_LOGIN;

import com.github.javafaker.Faker;
import core.models.user.StatusCreateUserModel;
import core.models.user.CreatingUserModel;
import core.models.user.GetUserModel;
import core.models.user.StatusLoginUserModel;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

public class UsersTests extends BaseTest {

  Faker faker = new Faker();

  String userID;
  String username = faker.name().username();
  String firstName = faker.name().firstName();
  String lastName = faker.name().lastName();
  String email = faker.internet().emailAddress();
  String password = faker.internet().password();
  String phone = faker.phoneNumber().phoneNumber();

  CreatingUserModel expectCreatingUserModel;

  // Task 6
  @Test
  public void checkThatCreatedNewUserTest() {

    // Request
    expectCreatingUserModel = CreatingUserModel.builder()
        .username(username)
        .firstName(firstName)
        .lastName(lastName)
        .email(email)
        .password(password)
        .phone(phone)
        .userStatus(10)
        .build();

    // Response
    ValidatableResponse userResponse = RestAssured
        .given()
        .body(expectCreatingUserModel)
        .when()
        .post(USER)
        .then()
        .statusCode(200);

    StatusCreateUserModel createUserStatusResponse = userResponse.extract()
        .as(StatusCreateUserModel.class);

    userID = createUserStatusResponse.getMessage();

    // Assertions
    SoftAssertions softAssertions = new SoftAssertions();
    // Check that message body not equals to 0
    softAssertions.assertThat(createUserStatusResponse.getMessage())
        .as("Message body equals to 0")
        .isNotEqualTo(0);

    softAssertions.assertAll();
  }

  // Task 7
  @Test(dependsOnMethods = "checkThatCreatedNewUserTest")
  public void checkThatAllDataUsersWasSavedTest() {

    ValidatableResponse userResponse = RestAssured
        .given()
        .pathParam("username", username)
        .when()
        .get(USER_BY_USERNAME)
        .then()
        .statusCode(200);

    GetUserModel getUserResponse = userResponse.extract().as(GetUserModel.class);

    // Assertions
    SoftAssertions softAssertions = new SoftAssertions();

    // Check that all data User was saved
    softAssertions.assertThat(expectCreatingUserModel)
        .as("All data User was not saved")
        .isEqualToIgnoringGivenFields(getUserResponse, "id");

    // Check that used ID = value of massage field from previous test
    softAssertions.assertThat(getUserResponse.getId())
        .as("We are waited user ID: [" + userID + "] but  and received: ["
            + getUserResponse.getId() + "]")
        .isEqualTo(userID);

    softAssertions.assertAll();
  }

  // Task 8
  @Test(dependsOnMethods = "checkThatCreatedNewUserTest")
  public void checkThatMessageContainsValueAfterLoggedTest() {

    String messageAfterLogin = "logged in user session:";

    ValidatableResponse userResponse = RestAssured
        .given()
        .queryParam("username", username)
        .queryParam("password", password)
        .when()
        .get(USER_LOGIN)
        .then()
        .statusCode(200);

    StatusLoginUserModel getUserResponse = userResponse.extract().as(StatusLoginUserModel.class);

    // Assertions
    SoftAssertions softAssertions = new SoftAssertions();

    // Check that message contains value is "message": "logged in user session:"
    softAssertions.assertThat(getUserResponse.getMessage())
        .as("Message not contains value: [" + messageAfterLogin + "]")
        .contains(messageAfterLogin);

    softAssertions.assertAll();
  }

}