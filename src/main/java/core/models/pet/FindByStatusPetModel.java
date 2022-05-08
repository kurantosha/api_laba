package core.models.pet;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@ToString
@Data
public class FindByStatusPetModel {

  private String id;
  private String name;
  private List<String> photoUrls;
  private String status;
  private PetModel.Category category;
  private List<PetModel.Tags> tags;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonInclude(NON_NULL)
  @ToString
  @Data
  public static class Category {

    private Integer id;
    private String name;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonInclude(NON_NULL)
  @ToString
  @Data
  public static class Tags {

    private Integer id;
    private String name;
  }

  public static List<String> getNamesPets(List<FindByStatusPetModel> pets) {
    List<String> petsNames = new ArrayList<>();
    for (FindByStatusPetModel pet : pets) {
      petsNames.add(pet.getName());
    }
    return petsNames;
  }

}
