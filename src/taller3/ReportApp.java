package taller3;

public class ReportApp {
    public static void main(String[] args) {
        String orderDetails = "Order #7721 - Total: $45.99 - Items: Burger, Fries, Soda";

        // Requesting a PDF Report
        ReportGenerator clientReport = ReportFactory.getReport("PDF");
        clientReport.generate(orderDetails);

        // Requesting an Excel Report for accounting
        ReportGenerator accountingReport = ReportFactory.getReport("EXCEL");
        accountingReport.generate(orderDetails);

        // Requesting a JSON Report for the mobile app
        ReportGenerator apiReport = ReportFactory.getReport("JSON");
        apiReport.generate(orderDetails);
    }
}