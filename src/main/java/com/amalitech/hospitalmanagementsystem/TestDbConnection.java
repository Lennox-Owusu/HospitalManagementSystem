
package com.amalitech.hospitalmanagementsystem;

import com.amalitech.hospitalmanagementsystem.util.DBConnectionUtil;
import java.sql.Connection;

public class TestDbConnection {
    public static void main(String[] args) {
        try (Connection conn = DBConnectionUtil.getConnection()) {
            System.out.println("✅ Connected to PostgreSQL successfully!");
        } catch (Exception e) {
            System.err.println("❌ Connection failed");
            e.printStackTrace();
        }
    }
}
