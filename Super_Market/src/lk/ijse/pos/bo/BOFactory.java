package lk.ijse.pos.bo;

import lk.ijse.pos.bo.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {
    }

    public static BOFactory getBoFactory() {
        if (boFactory == null) {
            boFactory = new BOFactory();
        }
        return boFactory;
    }

    public enum BOTypes {
        CUSTOMER, ITEM, ORDER_MANAGE, PURCHASE_ORDER, SYSTEM_REPORT
    }

    public SuperBO getBO(BOTypes types) {
        switch (types) {
            case CUSTOMER:
                return new CustomerBOImpl();
            case ITEM:
                return new ItemBOImpl();
            case ORDER_MANAGE:
                return new OrderManageBOImpl();
            case PURCHASE_ORDER:
                return new PurchaseOrderBOImpl();
            case SYSTEM_REPORT:
                return new SystemReportBOImpl();
            default:
                return null;
        }
    }
}
