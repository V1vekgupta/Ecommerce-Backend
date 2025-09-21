package learning.ecomb.service.Category;

import learning.ecomb.payload.CategoryDTO;
import learning.ecomb.payload.CategoryResponse;

public interface CategoryService {
    // This is service interface of category entity
    // It contains all the abstract methods of category entity
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}