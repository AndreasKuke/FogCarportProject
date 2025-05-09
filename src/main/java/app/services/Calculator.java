package app.services;

import app.entities.Order;
import app.entities.PartsList;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.PartMapper;
import app.persistence.PartVariantMapper;
import app.persistence.PartsListMapper;

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
        int numberOfPoles = 4; // Base pole amount for each corner.
        int length = order.getCarport_length();
        int maxPoleDistance = 340; // Jon's ord ikke mine.

        if (length > maxPoleDistance){
            int extraPoles = (length + maxPoleDistance - 1) / maxPoleDistance - 1; // Calculates amount of poles, minus base ones

            numberOfPoles += (extraPoles * 2);
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
        int numberOfRafters = (length + rafterSpacing - 1) / rafterSpacing + 1;
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
}
