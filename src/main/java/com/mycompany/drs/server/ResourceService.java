/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.drs.server;

/**
 *
 * @author shubh
 */
import com.mycompany.drs.database.DatabaseConnector;
import com.mycompany.drs.shared.MasterResource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceService {
    public List<MasterResource> getAllMasterResources() {
        List<MasterResource> resources = new ArrayList<>();
        String sql = "SELECT * FROM master_resources ORDER BY unit_type, unit_name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                MasterResource res = new MasterResource();
                res.setMasterId(rs.getInt("master_id"));
                res.setUnitName(rs.getString("unit_name"));
                res.setUnitType(rs.getString("unit_type"));
                res.setHomeStation(rs.getString("home_station"));
                res.setCurrentStatus(rs.getString("current_status"));
                resources.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resources;
    }
}