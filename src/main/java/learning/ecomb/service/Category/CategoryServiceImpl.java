package learning.ecomb.service.Category;

import learning.ecomb.exception.APIException;
import learning.ecomb.exception.ResourceNotFoundException;
import learning.ecomb.model.Category;
import learning.ecomb.payload.CategoryDTO;
import learning.ecomb.payload.CategoryResponse;
import learning.ecomb.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    /*
     * Summary
     * - Service for Category CRUD using CategoryRepository and ModelMapper.
     * - Comments below are concise notes and developer reminders only — code logic is unchanged.
     */

      /*
        * Dependencies are injected via @Autowired.
        * Consider constructor injection or setter injection for easier testing.
      */

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    /*
     * getAllCategories
     * - Returns a paginated, sorted CategoryResponse.
     * - Caller must ensure pageNumber/pageSize/sortBy/sortOrder are valid (null/invalid values may cause runtime errors).
     * - Note: Currently throws APIException when the returned page has no content. Returning an empty list is
     *   usually preferable for an empty result set.
     */
    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // NOTE: sortOrder may be null. Calling equalsIgnoreCase on null will throw NPE.
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // NOTE: pageNumber/pageSize may be null or negative. Validate or provide defaults at caller/controller level.
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty())
            throw new APIException("No category created till now.");

        // Map entities to DTOs
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    /*
     * createCategory
     * - Creates a new category after checking for existing name.
     * - Caveats: name-check may be case-sensitive; consider existsByCategoryNameIgnoreCase or a DB unique constraint.
     * - Be cautious: mapping DTO may copy an ID from DTO into entity; if strict create-only behavior is needed,
     *   explicitly null the ID before saving.
     */
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        if (categoryFromDb != null)
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists !!!");
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    /*
     * deleteCategory
     * - Deletes category if found, otherwise throws ResourceNotFoundException.
     * - Suggestion: add logging and @Transactional on write operations for clarity and atomicity.
     */
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    /*
     * updateCategory
     * - Current approach maps DTO to a new Category, sets the ID and saves it.
     * - Caveat: this can overwrite fields not present in the DTO and may discard JPA-managed relationships.
     * - Safer approach (not applied here per your request): update fields on the fetched entity and save it.
     */
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    /*
     * Additional developer reminders (non-blocking):
     * - Prefer constructor injection for easier testing.
     * - Validate inputs (DTO fields, paging params) at controller layer using @Valid.
     * - Add DB-level unique constraint on category name to avoid race conditions.
     * - Add logging for create/update/delete operations.
     */
}
