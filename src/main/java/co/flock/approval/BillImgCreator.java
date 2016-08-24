package co.flock.approval;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import co.flock.approval.database.Bill;

public class BillImgCreator
{
    private static final Logger _logger = Logger.getLogger(Runner.class);

    public void createBillImg(Bill bill)
    {
        final BufferedImage image;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            // Getting resource(File) from class loader
            File inputFIle = new File(classLoader.getResource("public/js/bill.png").getFile());

            image = ImageIO.read(inputFIle);
            Graphics g = image.getGraphics();
            g.setFont(new Font("default", Font.BOLD, 20));
            g.setColor(Color.black);
            g.drawString("Id : "+ bill.getId(), 100, 150);
            g.drawString("Amount : "+ bill.getAmount(), 100, 200);
            g.drawString("Date : "+ bill.getCreationDate(), 100, 250);
            g.drawString("Approver : "+ bill.getApproverName(), 100, 300);
            g.drawString("Status : "+ bill.getStatus(), 100, 350);

            g.dispose();
            _logger.debug("drawing img");
            inputFIle.getParent();
            File opFIle = new File(inputFIle.getParent() + "/bill" + bill.getId() + ".png");
            _logger.debug(opFIle.getPath());
            ImageIO.write(image, "png", opFIle);
        } catch (IOException e) {
            _logger.debug("Error in creating bill img : ", e);
        }

    }

    private static String getBillString(Bill bill)
    {
        return String
            .format("Id: %s \n CreatedOn: %s\nAmount: %s\nStatus: %s\nApprover: %s\n", bill.getId(),
                bill.getCreationDate(), bill.getAmount(), bill.getStatus(), bill.getApproverName());
    }
}
