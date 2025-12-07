/**
 * 
 */
package ch.cevi.db.client.business;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.BillFormat;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;

/**
 * @author mbaumgar
 *
 */
public class QRCodeBillingTest {

	@Test
	public void testStweg() {
		// Setup bill
        Bill bill = new Bill();
        bill.setAccount("CH2006850016404100005");
        bill.setAmountFromDouble(80.00);
        bill.setCurrency("CHF");

        // Set creditor
        Address creditor = new Address();
        creditor.setName("STWEG Rütibach 10");
        creditor.setAddressLine1("Rütibach 10");
        creditor.setAddressLine2("8340 Hinwil");

        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);

        // more bill data
        //bill.setReference("210000000003139471430009017");
        bill.setUnstructuredMessage("Miete Ersatzparkplatz Oktober-November 2022");

        // Set debtor
        Address debtor = new Address();
        debtor.setName("Gemeindeverwaltung Hinwil");
        debtor.setAddressLine1("Dürntnerstrasse 8");
        debtor.setAddressLine2("8340 Hinwil");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);

        // Set output format
        BillFormat format = bill.getFormat();
        format.setGraphicsFormat(GraphicsFormat.PNG);
        format.setOutputSize(OutputSize.QR_BILL_ONLY);
        format.setLanguage(Language.DE);

        // Generate QR bill
        byte[] imageData = QRBill.generate(bill);

        // Save QR bill
        Path path = Paths.get("parkplatzrechnung.png");
        try {
            Files.write(path, imageData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("QR bill saved at " + path.toAbsolutePath());
	}
	
	@Test
	public void temarcelstPrivat() {
		// Setup bill
        Bill bill = new Bill();
        bill.setAccount("CH5509000000876494648");
        bill.setAmountFromDouble(1529.00);
        bill.setCurrency("CHF");

        // Set creditor
        Address creditor = new Address();
        creditor.setName("Marc Baumgartner");
        creditor.setAddressLine1("Rütibach 10");
        creditor.setAddressLine2("8340 Hinwil");

        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);

        // more bill data
        //bill.setReference("210000000003139471430009017");
        bill.setUnstructuredMessage("Mietanteil Soedra Sandby");

        // Set debtor
        Address debtor = new Address();
        debtor.setName("Sandra Schittli");
        debtor.setAddressLine1("Kirchstrasse 12");
        debtor.setAddressLine2("9535 Wilen");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);

        // Set output format
        BillFormat format = bill.getFormat();
        format.setGraphicsFormat(GraphicsFormat.PDF);
        format.setOutputSize(OutputSize.QR_BILL_ONLY);
        format.setLanguage(Language.DE);

        // Generate QR bill
        byte[] imageData = QRBill.generate(bill);

        // Save QR bill
        Path path = Paths.get("SchittliSödraSundby.pdf");
        try {
            Files.write(path, imageData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("QR bill saved at " + path.toAbsolutePath());
	}
	
	@Test
	public void testCeviUsterJungschar() {
		// Setup bill
        Bill bill = new Bill();
        bill.setAccount("CH4209000000800503045");
        //bill.setAmountFromDouble(80.00);
        bill.setCurrency("CHF");

        // Set creditor
        Address creditor = new Address();
        creditor.setName("Cevi Uster, Jungschar");
        creditor.setAddressLine1("");
        creditor.setAddressLine2("8610 Uster");


        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);

        // more bill data
        //bill.setReference("210000000003139471430009017");
        bill.setUnstructuredMessage("Mitgliederbeitrag Ryan Müller");

        // Set debtor
        Address debtor = new Address();
        debtor.setName("Marc Müller");
        debtor.setAddressLine1("Winikerstrasse 3");
        debtor.setAddressLine2("8610 Uster");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);

        // Set output format
        BillFormat format = bill.getFormat();
        format.setGraphicsFormat(GraphicsFormat.PNG);
        format.setOutputSize(OutputSize.QR_BILL_ONLY);
        format.setLanguage(Language.DE);

        // Generate QR bill
        byte[] imageData = QRBill.generate(bill);

        // Save QR bill
        Path path = Paths.get("2022_Mitgliederbeitrag_Cevi_Uster_RyanMüller.png");
        try {
            Files.write(path, imageData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("QR bill saved at " + path.toAbsolutePath());
	}
	

}
