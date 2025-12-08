package constructpro.DAO;

import constructpro.DTO.Insurance;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsuranceDAO {
    final Connection connection;

    public InsuranceDAO(Connection connection) throws SQLException {
        this.connection = connection;
    }

    // CREATE - Add new insurance record
    public boolean addInsurance(Insurance insurance) throws SQLException {
        String sql = """
                INSERT INTO Insurance (worker_id, insurance_number, agency_name, status,
                                     start_date, end_date, insurance_documents)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, insurance.getWorkerId());
            pstmt.setString(2, insurance.getInsuranceNumber());
            pstmt.setString(3, insurance.getAgencyName());
            pstmt.setString(4, insurance.getStatus());
            pstmt.setDate(5, insurance.getStartDate() != null ? Date.valueOf(insurance.getStartDate()) : null);
            pstmt.setDate(6, insurance.getEndDate() != null ? Date.valueOf(insurance.getEndDate()) : null);

            // Convert document list to comma-separated string
            String documentsStr = insurance.getInsuranceDocuments() != null
                    ? String.join(",", insurance.getInsuranceDocuments())
                    : "";
            pstmt.setString(7, documentsStr);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        insurance.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    // READ - Get insurance by worker ID
    public Insurance getInsuranceByWorkerId(int workerId) throws SQLException {
        String sql = """
                SELECT id, worker_id, insurance_number, agency_name, status,
                       start_date, end_date, insurance_documents
                FROM Insurance
                WHERE worker_id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, workerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInsurance(rs);
                }
            }
        }
        return null;
    }

    public Insurance getInsuranceById(int insuranceId) throws SQLException {
        String sql = """
                SELECT id, worker_id, insurance_number, agency_name, status,
                       start_date, end_date, insurance_documents
                FROM Insurance
                WHERE id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, insuranceId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInsurance(rs);
                }
            }
        }
        return null;
    }

    public List<Insurance> getAllInsurances() throws SQLException {
        String sql = """
                SELECT id, worker_id, insurance_number, agency_name, status,
                       start_date, end_date, insurance_documents
                FROM Insurance
                ORDER BY id
                """;

        List<Insurance> insurances = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                insurances.add(mapResultSetToInsurance(rs));
            }
        }

        return insurances;
    }

    public List<Insurance> getInsurancesByStatus(String status) throws SQLException {
        String sql = """
                SELECT id, worker_id, insurance_number, agency_name, status,
                       start_date, end_date, insurance_documents
                FROM Insurance
                WHERE LOWER(status) = LOWER(?)
                ORDER BY id
                """;

        List<Insurance> insurances = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    insurances.add(mapResultSetToInsurance(rs));
                }
            }
        }

        return insurances;
    }

    public List<Insurance> getExpiringInsurances(int daysFromNow) throws SQLException {
        String sql = """
                SELECT id, worker_id, insurance_number, agency_name, status,
                       start_date, end_date, insurance_documents
                FROM Insurance
                WHERE end_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY)
                AND end_date >= CURDATE()
                AND LOWER(status) = 'active'
                ORDER BY end_date ASC
                """;

        List<Insurance> insurances = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, daysFromNow);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    insurances.add(mapResultSetToInsurance(rs));
                }
            }
        }

        return insurances;
    }

    public boolean updateInsurance(Insurance insurance) throws SQLException {
        String sql = """
                UPDATE Insurance
                SET insurance_number = ?, agency_name = ?, status = ?,
                    start_date = ?, end_date = ?, insurance_documents = ?
                WHERE id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, insurance.getInsuranceNumber());
            pstmt.setString(2, insurance.getAgencyName());
            pstmt.setString(3, insurance.getStatus());
            pstmt.setDate(4, insurance.getStartDate() != null ? Date.valueOf(insurance.getStartDate()) : null);
            pstmt.setDate(5, insurance.getEndDate() != null ? Date.valueOf(insurance.getEndDate()) : null);

            // Convert document list to comma-separated string
            String documentsStr = insurance.getInsuranceDocuments() != null
                    ? String.join(",", insurance.getInsuranceDocuments())
                    : "";
            pstmt.setString(6, documentsStr);
            pstmt.setInt(7, insurance.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateInsuranceStatus(int insuranceId, String status) throws SQLException {
        String sql = "UPDATE Insurance SET status = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, insuranceId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean addDocumentToInsurance(int insuranceId, String documentName) throws SQLException {
        Insurance insurance = getInsuranceById(insuranceId);
        if (insurance == null) {
            return false;
        }

        List<String> documents = insurance.getInsuranceDocuments();
        if (documents == null) {
            documents = new ArrayList<>();
        }

        // Check if document already exists
        if (!documents.contains(documentName)) {
            documents.add(documentName);
            insurance.setInsuranceDocuments(documents);
            return updateInsurance(insurance);
        }

        return true; // Document already exists
    }

    public boolean removeDocumentFromInsurance(int insuranceId, String documentName) throws SQLException {
        Insurance insurance = getInsuranceById(insuranceId);
        if (insurance == null) {
            return false;
        }

        List<String> documents = insurance.getInsuranceDocuments();
        if (documents != null && documents.contains(documentName)) {
            documents.remove(documentName);
            insurance.setInsuranceDocuments(documents);
            return updateInsurance(insurance);
        }

        return true; // Document doesn't exist
    }

    // DELETE - Delete insurance by ID
    public boolean deleteInsurance(int insuranceId) throws SQLException {
        String sql = "DELETE FROM Insurance WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, insuranceId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // DELETE - Delete insurance by worker ID
    public boolean deleteInsuranceByWorkerId(int workerId) throws SQLException {
        String sql = "DELETE FROM Insurance WHERE worker_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, workerId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // UTILITY - Check if worker has insurance
    public boolean workerHasInsurance(int workerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Insurance WHERE worker_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, workerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // UTILITY - Get submitted documents for an insurance
    public List<String> getSubmittedDocuments(int insuranceId) throws SQLException {
        Insurance insurance = getInsuranceById(insuranceId);
        if (insurance != null && insurance.getInsuranceDocuments() != null) {
            return new ArrayList<>(insurance.getInsuranceDocuments());
        }
        return new ArrayList<>();
    }

    // UTILITY - Get missing documents for an insurance
    public List<String> getMissingDocuments(int insuranceId) throws SQLException {
        List<String> requiredDocuments = Arrays.asList(
                "Acte de Naissance",
                "Fiche familiale de l'état civil",
                "Photocopie de la carte identité",
                "Photocopie de chèque");

        List<String> submittedDocuments = getSubmittedDocuments(insuranceId);
        List<String> missingDocuments = new ArrayList<>();

        for (String required : requiredDocuments) {
            if (!submittedDocuments.contains(required)) {
                missingDocuments.add(required);
            }
        }

        return missingDocuments;
    }

    // UTILITY - Check if all required documents are submitted
    public boolean areAllDocumentsSubmitted(int insuranceId) throws SQLException {
        return getMissingDocuments(insuranceId).isEmpty();
    }

    // UTILITY - Get document completion percentage
    public double getDocumentCompletionPercentage(int insuranceId) throws SQLException {
        List<String> requiredDocuments = Arrays.asList(
                "Acte de Naissance",
                "Fiche familiale de l'état civil",
                "Photocopie de la carte identité",
                "Photocopie de chèque");

        List<String> submittedDocuments = getSubmittedDocuments(insuranceId);

        if (requiredDocuments.isEmpty()) {
            return 100.0;
        }

        int submittedCount = 0;
        for (String required : requiredDocuments) {
            if (submittedDocuments.contains(required)) {
                submittedCount++;
            }
        }

        return (submittedCount * 100.0) / requiredDocuments.size();
    }

    // HELPER - Map ResultSet to Insurance object
    private Insurance mapResultSetToInsurance(ResultSet rs) throws SQLException {
        Insurance insurance = new Insurance();

        insurance.setId(rs.getInt("id"));
        insurance.setWorkerId(rs.getInt("worker_id"));
        insurance.setInsuranceNumber(rs.getString("insurance_number"));
        insurance.setAgencyName(rs.getString("agency_name"));
        insurance.setStatus(rs.getString("status"));

        // Handle dates
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            insurance.setStartDate(startDate.toLocalDate());
        }

        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            insurance.setEndDate(endDate.toLocalDate());
        }

        String documentsStr = rs.getString("insurance_documents");
        if (documentsStr != null && !documentsStr.trim().isEmpty()) {
            List<String> documents = Arrays.asList(documentsStr.split(","));
            documents = documents.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            insurance.setInsuranceDocuments(documents);
        } else {
            insurance.setInsuranceDocuments(new ArrayList<>());
        }

        return insurance;
    }
}
