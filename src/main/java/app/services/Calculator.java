package app.services;

import app.entities.Order;
import app.entities.PartsList;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.PartMapper;
import app.persistence.PartVariantMapper;
import app.persistence.PartsListMapper;
import io.javalin.http.Context;

public class Calculator {


    private ConnectionPool connectionPool;
    private PartVariantMapper partVariantMapper;
    private PartMapper partMapper;

    public Calculator(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.partVariantMapper = new PartVariantMapper(connectionPool);
        this.partMapper = new PartMapper(connectionPool);
    }

    public void calcPoles(Order order) throws DatabaseException {
        PartsListMapper partsListMapper = new PartsListMapper(connectionPool);
        int numberOfPoles = 2; // Two base poles at the end.
        int length = order.getCarport_length();
        int maxPoleDistance = 340; // Jon's ord ikke mine.
        int initialSpacing = 100; // Luftrum mellem indgang og første pæl.

        int fullLength = length - initialSpacing; // Fjernet 100 cm luftrum fra start.

        if (fullLength > 0){
            //numberOfPoles += (length - 1) / maxPoleDistance;
            numberOfPoles = 2 * ((fullLength + maxPoleDistance - 1) / maxPoleDistance + 1);
        } else {
            numberOfPoles = 4;
        }


        Integer polePartId = partMapper.getPartIdByName("97x97 mm. trykimp. Stolpe");
        if (polePartId != null) {
            Integer poleVariantId = partVariantMapper.findVariantByLengthAndPart(300, polePartId);
            if (poleVariantId != null) {
                partsListMapper.insertPartListItem(order.getOrder_ID(), poleVariantId, numberOfPoles, "Stolper nedgraves 90 cm. i jorden");
            }else {
                throw new DatabaseException("Part variant not found");
            }
        }else {
            throw new DatabaseException("Part not found");
        }
    }

    // TO-DO, tilføj så den kan sætte flere sammen end kun 2.
    public void calcBeams(Order order) throws DatabaseException {
        PartsListMapper partsListMapper = new PartsListMapper(connectionPool);
        int length = order.getCarport_length();
        int numberOfBeams = 2;

        if(length > 600){
            extraBeams(order, length);
        }

        Integer beamPartId = partMapper.getPartIdByName("45x195 mm. spærtræ ubh.");
        if (beamPartId != null) {
            Integer beamVariantId = partVariantMapper.findVariantByLengthAndPart(length, beamPartId);
            if (beamVariantId != null) {
                partsListMapper.insertPartListItem(order.getOrder_ID(), beamVariantId,
                        numberOfBeams, "Remme i sider, sadles ned i stolpen");
            }else {
                throw new DatabaseException("Part variant not found");
            }
        }else {
            throw new DatabaseException("Part not found");
        }
    }

    // TO-DO, lige nu kan den kun tilføje bredde = længde for spærtræ i db.
    public void calcRafters(Order order) throws DatabaseException {
        PartsListMapper partsListMapper = new PartsListMapper(connectionPool);
        int length = order.getCarport_length();
        int width = order.getCarport_width();
        int rafterSpacing = 55;
        int numberOfRafters = (length + rafterSpacing - 1) / rafterSpacing;
        int rafterLength = width;

        Integer rafterPartId = partMapper.getPartIdByName("45x195 mm. spærtræ ubh.");
        if (rafterPartId != null) {
            Integer rafterVariantId = partVariantMapper.findVariantByLengthAndPart(rafterLength, rafterPartId);
            if (rafterVariantId != null) {
                partsListMapper.insertPartListItem(order.getOrder_ID(), rafterVariantId, numberOfRafters, "Spær monteres på rem");
            } else {
                throw new DatabaseException("Part variant not found");
            }
        }else {
            throw new DatabaseException("Part not found");
        }
    }

    public void extraBeams(Order order, int length) throws DatabaseException {
        PartsListMapper partsListMapper = new PartsListMapper(connectionPool);

        int restLength = length-600;
        if(restLength <= 120){
            restLength = 120;
        }

        Integer beamPartId = partMapper.getPartIdByName("45x195 mm. spærtræ ubh.");
        if (beamPartId != null) {
            Integer beamVariantId = partVariantMapper.findVariantByLengthAndPart(restLength*2, beamPartId);
            if (beamVariantId != null) {
                partsListMapper.insertPartListItem(order.getOrder_ID(), beamVariantId, 1, "Remme i sider, sadles ned i stolpen, deles i to");
            }else {
                throw new DatabaseException("Part variant not found");
            }
        }else {
            throw new DatabaseException("Part not found");
        }
    }

    // Count number of poles, beams and rafters methods since the previous methods dont return anything
    private int countPoles(Order order) {
        int numberOfPoles = 2; // Two base poles at the end
        int length = order.getCarport_length();
        int maxPoleDistance = 340;
        int initialSpacing = 100;

        int fullLength = length - initialSpacing;

        if (fullLength > 0) {
            numberOfPoles = 2 * ((fullLength + maxPoleDistance - 1) / maxPoleDistance + 1);
        } else {
            numberOfPoles = 4;
        }

        return numberOfPoles;
    }

    private int countBeams(Order order) {
        int numberOfBeams = 2; // Standard is 2 beams
        int length = order.getCarport_length();

        if (length > 600) {
            numberOfBeams += 1;
        }

        return numberOfBeams;
    }

    private int countRafters(Order order) {
        int length = order.getCarport_length();
        int rafterSpacing = 55;

        return (length + rafterSpacing - 1) / rafterSpacing;
    }


    public int calcPrice(Order order) throws DatabaseException {
        int length = order.getCarport_length();
        int width = order.getCarport_width();

        int numberOfPoles = countPoles(order);
        int numberOfBeams = countBeams(order);
        int numberOfRafters = countRafters(order);

        int poleUnitPrice = partMapper.getPartByName("97x97 mm. trykimp. Stolpe").getPrice();
        int beamUnitPricePerMeter = partMapper.getPartByName("45x195 mm. spærtræ ubh.").getPrice();
        int rafterUnitPricePerMeter = beamUnitPricePerMeter;

        int totalPolePrice = numberOfPoles * poleUnitPrice * 3; // each pole is 3 meters
        int totalBeamPrice = numberOfBeams * (length / 100) * beamUnitPricePerMeter;
        int totalRafterPrice = numberOfRafters * (width / 100) * rafterUnitPricePerMeter;

        return totalPolePrice + totalBeamPrice + totalRafterPrice;
    }

}
