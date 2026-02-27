package taller3;

public interface ReportGenerator {
    void generate(String data);
}

// PDF Implementation
class PdfReport implements ReportGenerator {
    @Override
    public void generate(String data) {
        System.out.println("[PDF] Generating .pdf file with content: " + data);
    }
}

// Excel Implementation
class ExcelReport implements ReportGenerator {
    @Override
    public void generate(String data) {
        System.out.println("[Excel] Creating .xlsx spreadsheet with content: " + data);
    }
}

// JSON Implementation (Useful for API responses)
class JsonReport implements ReportGenerator {
    @Override
    public void generate(String data) {
        System.out.println("[JSON] Structuring data into JSON format: { \"report_data\": \"" + data + "\" }");
    }
}