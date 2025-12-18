package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao
{
    public MySqlProductDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String subCategory)
    {
        // This list holds all matching products found in the database
        List<Product> products = new ArrayList<>();

    /*

        We want to be able to filter by ANY combination of filters the user provides, so..
        we use values to mean - ignore this filter:
            categoryId = -1     = ignore category filter
            minPrice = -1       = ignore min price filter
            maxPrice = -1       = ignore max price filter
            subCategory = ""    = ignore subcategory filter

        Example:
        - If categoryId is 2:
            (category_id = 2 or 2 = -1)  only category 2 matches
        - If categoryId is -1:
            (category_id = -1 or -1 = -1) => TRUE for every row (filter ignored)
     */
        String sql =
                "SELECT * FROM products " +
                        "WHERE (category_id = ? OR ? = -1) " +          // category filter (or ignore)
                        "  AND (price >= ? OR ? = -1) " +               // minPrice filter (or ignore)
                        "  AND (price <= ? OR ? = -1) " +               // maxPrice filter (or ignore)
                        "  AND (subcategory = ? OR ? = '') ";           // subCategory filter (or ignore)

        // If a filter was not provided (null), this converts it into a value that means "ignore"
        categoryId = (categoryId == null) ? -1 : categoryId;               // -1 means ignore category filter
        minPrice = (minPrice == null) ? new BigDecimal("-1") : minPrice;   // -1 means ignore min price filter
        maxPrice = (maxPrice == null) ? new BigDecimal("-1") : maxPrice;   // -1 means ignore max price filter
        subCategory = (subCategory == null) ? "" : subCategory;            // "" means ignore subcategory filter

        try (Connection connection = getConnection())
        {
            // Prepares the SQL statement so it can plug in values using ?
            PreparedStatement statement = connection.prepareStatement(sql);

            // 1-2 category filter values
            statement.setInt(1, categoryId);  // category_id = ?
            statement.setInt(2, categoryId);  // ? = -1 means ignore

            // 3-4 min price filter values
            statement.setBigDecimal(3, minPrice); // price >= ?
            statement.setBigDecimal(4, minPrice); // ? = -1 means ignore

            // 5-6 max price filter values
            statement.setBigDecimal(5, maxPrice); // price <= ?
            statement.setBigDecimal(6, maxPrice); // ? = -1 means ignore

            // 7-8 subcategory filter values
            statement.setString(7, subCategory);  // subcategory = ?
            statement.setString(8, subCategory);  // ? = " " means ignore

            // Executes the query and gets matching rows as a result
            ResultSet row = statement.executeQuery();

            // this loops through each row returned and converts it into a Product object
            while (row.next())
            {
                Product product = mapRow(row); // mapRow reads columns and builds a Product
                products.add(product);         // adds p roduct to the result list
            }
        }
        catch (SQLException e)
        {
            // If SQL fails, crashes in a way so controller returns 500
            throw new RuntimeException(e);
        }

        // Returns the filtered list of products
        return products;
    }


    @Override
    public List<Product> listByCategoryId(int categoryId)
    {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products " +
                    " WHERE category_id = ? ";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);

            ResultSet row = statement.executeQuery();

            while (row.next())
            {
                Product product = mapRow(row);
                products.add(product);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return products;
    }


    @Override
    public Product getById(int productId)
    {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, productId);

            ResultSet row = statement.executeQuery();

            if (row.next())
            {
                return mapRow(row);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Product create(Product product)
    {

        String sql = "INSERT INTO products(name, price, category_id, description, subcategory, image_url, stock, featured) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getSubCategory());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    int orderId = generatedKeys.getInt(1);

                    // get the newly inserted category
                    return getById(orderId);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(int productId, Product product)
    {
        String sql = "UPDATE products" +
                " SET name = ? " +
                "   , price = ? " +
                "   , category_id = ? " +
                "   , description = ? " +
                "   , subcategory = ? " +
                "   , image_url = ? " +
                "   , stock = ? " +
                "   , featured = ? " +
                " WHERE product_id = ?;";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getSubCategory());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());
            statement.setInt(9, productId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int productId)
    {

        String sql = "DELETE FROM products " +
                " WHERE product_id = ?;";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, productId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected static Product mapRow(ResultSet row) throws SQLException
    {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String subCategory = row.getString("subcategory");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, subCategory, stock, isFeatured, imageUrl);
    }
}
