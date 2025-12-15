package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;              // Interface
import org.yearup.models.Category;               // Model

import javax.sql.DataSource;                     // Provides database connections
import java.sql.*;                               // JDBC classes
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        // Passes the DataSource to MySqlDaoBase so getConnection() works
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // holds all categories from the DB
        List<Category> categories = new ArrayList<>();

        // query to pull all categories
        String sql = "SELECT category_id, name, description FROM categories";

        try (Connection connection = getConnection();                // Opens a DB connection
             PreparedStatement statement = connection.prepareStatement(sql); // Prepares SQL
             ResultSet row = statement.executeQuery())               // Executes query and gets results
        {
            // Loops through each row returned by the query
            while (row.next())
            {
                // this Converts the row into a Category object using mapRow helper
                Category category = mapRow(row);

                // Adds it to the list
                categories.add(category);
            }
        }
        catch (SQLException e)
        {
            // Throw runtime exception so controller returns 500
            throw new RuntimeException("Error getting all categories", e);
        }

        // Return the list as json to the controller
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        // query to get one category by id
        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection(); // Opens database connection
             PreparedStatement statement = connection.prepareStatement(sql)) // Prepares SQL
        {
            // categoryId into the placeholder
            statement.setInt(1, categoryId);

            // Runs the query
            try (ResultSet row = statement.executeQuery())
            {
                // If nothing is  returned, category doesn't exist
                if (!row.next())
                {
                    return null;
                }

                // Converts row to Category and returns it
                return mapRow(row);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error getting category by id", e);
        }
    }

    @Override
    public Category create(Category category)
    {
        // SQL insert
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection connection = getConnection(); // Opens database connection
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            // Fills SQL placeholders with values from category object
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());

            // Run insert
            statement.executeUpdate();

            // Gets generated primary key (category_id)
            try (ResultSet keys = statement.getGeneratedKeys())
            {
                if (keys.next())
                {
                    // Saves new id back into the category object
                    category.setCategoryId(keys.getInt(1));
                }
            }

            // Returns the created object with the id now set
            return category;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error creating category", e);
        }
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // SQL updates
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

        try (Connection connection = getConnection(); // Open DB connection
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            // Fills placeholders with category values
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());

            // Fills the where id
            statement.setInt(3, categoryId);

            // Executes update
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        // SQL delete
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection(); // Open DB connection
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            // Fill placeholder with id
            statement.setInt(1, categoryId);

            // Executes delete
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error deleting category", e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        // Reads database columns from this row
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        // Creates a Category object and sets its fields
        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }
}
