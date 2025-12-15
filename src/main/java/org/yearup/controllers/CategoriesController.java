package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired; // Lets Spring inject dependencies into constructor
import org.springframework.http.HttpStatus;                  // HTTP status codes like 201, 404, etc
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;            // Spring REST annotations RestController, GetMapping
import org.springframework.web.server.ResponseStatusException;   // return errors like 404/500
import org.yearup.data.CategoryDao;                          // DAO interface for categories
import org.yearup.data.ProductDao;                           // DAO interface for products
import org.yearup.models.Category;                           // Category model
import org.yearup.models.Product;                            // Product model

import java.util.List;

@RestController // Makes this class a REST controller that returns JSON
@RequestMapping("/categories") // Base URL for this controller which is: http://localhost:8080/categories
@CrossOrigin // Allows the website to call my API
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;

    // Spring will inject the DAOs into this controller
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao)
    {
        // this saves the injected DAOs so our methods can use them
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    @GetMapping("") // Get /categories
    public List<Category> getAll()
    {
        try
        {
            // this Ask the dao for all categories and return them as json
            return categoryDao.getAllCategories();
        }
        catch (Exception ex)
        {
            // If something goes wrong this, returns 500
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting categories.");
        }
    }

    @GetMapping("/{id}") // get /categories/ id
    public Category getById(@PathVariable int id)
    {
        try
        {
            // asks the dao for the category with this id
            Category category = categoryDao.getById(id);

            // If the dao returns null, that means the id wasn't found - so it returns 404
            if (category == null)
            {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
            }

            // Returns the category as json
            return category;
        }
        catch (ResponseStatusException ex)
        {
            // If its already decided it's a 404 or another status, rethrows the error unchanged
            throw ex;
        }
        catch (Exception ex)
        {
            // Any unexpected error becomes a 500
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting category.");
        }
    }

    // This url returns all products in category 1:
    // http://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products") // get /categories/categoryId/products
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        try
        {
            // Asks productDao for products in this category
            return productDao.listByCategoryId(categoryId);
        }
        catch (Exception ex)
        {
            // If something fails, returns 500
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting products by category.");
        }
    }

    @PostMapping("") // post /categories
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Only admin can create categories
    @ResponseStatus(HttpStatus.CREATED) // If it is successful, returns 201
    public Category addCategory(@RequestBody Category category)
    {
        try
        {
            // Inserts the category and returns the created category
            return categoryDao.create(category);
        }
        catch (Exception ex)
        {
            // Any error becomes 500
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating category.");
        }
    }

    @PutMapping("/{id}") // put /categories/id
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Only admins can update categories
    @ResponseStatus(HttpStatus.NO_CONTENT) // Update success should return 204
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        try
        {
            // makes sure the category object has the same id as the URL
            category.setCategoryId(id);

            // If the id doesn't exist, the dao might update 0 rows.
            // this will check the existence first to return a clean 404 status
            Category existing = categoryDao.getById(id);
            if (existing == null)
            {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
            }

            // Performs the update
            categoryDao.update(id, category);
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating category.");
        }
    }

    @DeleteMapping("/{id}") // DELETE /categories/id
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Only ADMIN can delete categories
    @ResponseStatus(HttpStatus.NO_CONTENT) // Delete success should return 204
    public void deleteCategory(@PathVariable int id)
    {
        try
        {
            // this checks if it exists first so it can return 404 if not found
            Category existing = categoryDao.getById(id);
            if (existing == null)
            {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
            }

            // Deletes by id
            categoryDao.delete(id);
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting category.");
        }
    }
}
