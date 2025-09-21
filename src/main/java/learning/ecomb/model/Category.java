package learning.ecomb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor

// @Data generates getters, setters, toString, equals, and hashCode methods
// @NoArgsConstructor generates a no-argument constructor
// @AllArgsConstructor generates a constructor with all fields as parameters
// @Entity annotation marks this class as a JPA entity, which will be mapped to a database table
// The table name is specified as "categories"
// @Id annotation marks the field as the primary key of the entity
// @GeneratedValue annotation specifies that the primary key value will be generated automatically by the database;5
// @NotBlank annotation ensures that the categoryName field is not null or empty
// @Size annotation specifies that the categoryName must be at least 5 characters long.
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    @NotBlank
    @Size(min = 5, message = "Category name must contain atleast 5 characters")
    private String categoryName;

    // Relation to Product can be added here.
    // @OneToMany annotation specifies a one-to-many relationship with the Product entity
    // mappedBy indicates that the Product entity has a field named "category" that maps to this entity
    // cascade = CascadeType.ALL means that all operations (persist, merge, remove, refresh, detach) will be cascaded to the related Product entities
    // products is a list of Product entities that belong to this category
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}
