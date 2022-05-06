package core.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class PetModel {

  private Long id;
  private String name;
  private List<String> photoUrls;
  private String status;
  private Category category;
  private List<Tags> tags;

  private int code;
  private String type;
  private String message;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonInclude(NON_NULL)
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
  public static class Tags {

    private Integer id;
    private String name;
  }

  public static List<String> getTagsName(PetModel petModel){
    List<String> tagsName = new ArrayList<>();
    for (Tags tag : petModel.getTags()) {
      tagsName.add(tag.getName());
    }
    return tagsName;
  }

}