package com.store.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.store.app.model.Cart;
import com.store.app.model.Product;

public class ProductDao {
	private Connection con;
	private String query;
	private PreparedStatement smt;
	private ResultSet rs;
	
	public ProductDao(Connection con) {
		super();
		this.con = con;
	}
	
	public List<Product> showProducts(){
		List<Product> products = new ArrayList<Product>();
		try {
			query = "select * from products";
			smt = this.con.prepareStatement(query);
			rs = smt.executeQuery();
			while(rs.next()) {
				Product product_row = new Product();
				product_row.setId(rs.getInt("id"));
				product_row.setName(rs.getString("name"));
				product_row.setCategory(rs.getString("category"));
				product_row.setPrice(rs.getDouble("price"));
				product_row.setImage(rs.getString("image"));
				products.add(product_row);
			}
				
			}catch(Exception e) {
				e.printStackTrace();
		}
		return products;
	}
	
	public Product getSingleProduct(int id) {
		// Validate input parameter
		if (id <= 0) {
			return null;
		}
		
		// Use explicit column names for better performance and clarity
		final String SQL = "SELECT id, name, category, price FROM products WHERE id = ?";
		
		Product product = null;
		
		try (PreparedStatement pstmt = this.con.prepareStatement(SQL)) {
			pstmt.setInt(1, id);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				// Use if instead of while since id is unique
				if (rs.next()) {
					product = new Product();
					product.setId(rs.getInt("id"));
					product.setName(rs.getString("name"));
					product.setCategory(rs.getString("category"));
					product.setPrice(rs.getDouble("price"));
				}
			}
			
		} catch (SQLException e) {
			// Proper exception handling with specific SQL exception
			logError("Error fetching product with id: " + id, e);
		}
		
		return product;
	}
	
	/**
	 * Logs error messages for database operations
	 * @param message The error message
	 * @param e The exception that occurred
	 */
	private void logError(String message, SQLException e) {
		// Print stack trace for debugging
		e.printStackTrace();
		// Log message for production logging framework
		System.err.println(message + " - " + e.getMessage());
	}
	
	public List<Cart> getCartProduct(ArrayList<Cart> cartList){
		List<Cart> productList = new ArrayList<Cart>();
		try {
			if(cartList.size() > 0) {
				for(Cart item:cartList) {
					query = "select * from products where id=?";
					smt = this.con.prepareStatement(query);
					smt.setInt(1, item.getId());
					rs = smt.executeQuery();
					while(rs.next()) {
						Cart row = new Cart();
							row.setId(rs.getInt("id"));
							row.setName(rs.getString("name"));
							row.setCategory(rs.getString("category"));
							row.setPrice(rs.getDouble("price")*item.getQuantity());
							row.setQuantity(item.getQuantity());
							productList.add(row);
						}
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		  return productList ;
		}
	
	public double totalPrice(ArrayList<Cart> cartList) {
		double total=0;
		try {
			if(cartList.size() > 0) {
				for(Cart item:cartList) {
					query = "select price from products where id=?";
					smt = this.con.prepareStatement(query);
					smt.setInt(1, item.getId());
					rs = smt.executeQuery();
					while(rs.next()) {
						total+=rs.getDouble("price")*item.getQuantity();
					}
					
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return total;
		
	}
		
		
	}

