package com.openbravo.pos.forms;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMAttribute;

/**
 * This is a Client progam that accesses 'MyService' web service in Axis2 samples
 */
public class SAOPSync {

    private static String toEpr = "http://1.179.138.134:3080/webtools/control/SOAPService";
    static String test ="<entity-engine-xml><OrderHeader orderId=\"XXXX_B2C_CA01\" orderTypeId=\"SALES_ORDER\" orderName=\"Demo Sales Order B2C CA1\" salesChannelEnumId=\"B2C_CA_SALES_CHANNEL\" orderDate=\"2009-12-01 9:00:00.000\" entryDate=\"2009-12-01 9:00:00.000\" priority=\"2\" statusId=\"ORDER_APPROVED\" createdBy=\"admin\" currencyUom=\"USD\" productStoreId=\"B2CStore\" remainingSubTotal=\"12.0\" grandTotal=\"14.0\"/>" +
      	"<OrderItem orderId=\"XXXX_B2C_CA01\" orderItemSeqId=\"00001\" orderItemTypeId=\"PRODUCT_ORDER_ITEM\" productId=\"GZ-1001\" isPromo=\"N\" quantity=\"1.0\" selectedAmount=\"0.0\" unitPrice=\"12.0\" unitListPrice=\"25.99\" isModifiedPrice=\"Y\" itemDescription=\"Nan Gizmo\" correspondingPoId=\"\" statusId=\"ITEM_APPROVED\"/>" +
    "<OrderRole orderId=\"XXXX_B2C_CA01\" partyId=\"DemoCustomer\" roleTypeId=\"PLACING_CUSTOMER\"/>" +
    "<OrderRole orderId=\"XXXX_B2C_CA01\" partyId=\"DemoCustomer\" roleTypeId=\"BILL_TO_CUSTOMER\"/>" +
    "<OrderRole orderId=\"XXXX_B2C_CA01\" partyId=\"Company\" roleTypeId=\"BILL_FROM_VENDOR\"/>" +
    "<OrderRole orderId=\"XXXX_B2C_CA01\" partyId=\"DemoCustomer\" roleTypeId=\"SHIP_TO_CUSTOMER\"/>" +
    "<OrderRole orderId=\"XXXX_B2C_CA01\" partyId=\"DemoCustomer\" roleTypeId=\"END_USER_CUSTOMER\"/>" +
    "<OrderItemShipGroup orderId=\"XXXX_B2C_CA01\" shipGroupSeqId=\"00001\" shipmentMethodTypeId=\"NEXT_DAY\" carrierPartyId=\"UPS\" carrierRoleTypeId=\"CARRIER\" contactMechId=\"9015\" maySplit=\"N\" giftMessage=\"\" isGift=\"N\"/>" +
    "<OrderItemShipGroupAssoc orderId=\"XXXX_B2C_CA01\" orderItemSeqId=\"00001\" shipGroupSeqId=\"00001\" quantity=\"1.0\"/>" +
    "<OrderItemShipGrpInvRes orderId=\"XXXX_B2C_CA01\" shipGroupSeqId=\"00001\" orderItemSeqId=\"00001\" inventoryItemId=\"9001\" reserveOrderEnumId=\"INVRO_FIFO_REC\" quantity=\"1.0\" reservedDatetime=\"2009-12-01 9:00:00.000\" createdDatetime=\"2009-12-01 9:00:00.000\" promisedDatetime=\"2009-12-01 9:00:00.000\" priority=\"2\"/>" +
    "<OrderAdjustment orderAdjustmentId=\"XXXX_B2C_CA01_01\" orderAdjustmentTypeId=\"SHIPPING_CHARGES\" orderId=\"XXXX_B2C_CA01\" orderItemSeqId=\"_NA_\" shipGroupSeqId=\"_NA_\" amount=\"2\" createdDate=\"2009-12-01 9:00:00.000\" createdByUserLogin=\"admin\"/>" +
    "<OrderPaymentPreference orderPaymentPreferenceId=\"XXXX_B2C_CA01_01\" orderId=\"XXXX_B2C_CA01\" paymentMethodTypeId=\"EXT_EBAY\" presentFlag=\"N\" swipedFlag=\"N\" overflowFlag=\"N\" maxAmount=\"14.0\" statusId=\"PAYMENT_NOT_RECEIVED\"/>" +
    "<OrderStatus orderStatusId=\"XXXX_B2C_CA01_01\" statusId=\"ORDER_CREATED\" orderId=\"XXXX_B2C_CA01\" statusDatetime=\"2009-12-01 9:00:00.000\" statusUserLogin=\"admin\"/>" +
    "<OrderStatus orderStatusId=\"XXXX_B2C_CA01_02\" statusId=\"ITEM_CREATED\" orderId=\"XXXX_B2C_CA01\" orderItemSeqId=\"00001\" statusDatetime=\"2009-12-01 9:00:00.000\" statusUserLogin=\"admin\"/>" +
    "<OrderStatus orderStatusId=\"XXXX_B2C_CA01_03\" statusId=\"PAYMENT_NOT_RECEIVED\" orderId=\"XXXX_B2C_CA01\" orderPaymentPreferenceId=\"9000\" statusDatetime=\"2009-12-01 9:00:00.000\" statusUserLogin=\"admin\"/>" +
    "<OrderStatus orderStatusId=\"XXXX_B2C_CA01_04\" statusId=\"ORDER_APPROVED\" orderId=\"XXXX_B2C_CA01\" statusDatetime=\"2009-12-01 9:00:00.000\" statusUserLogin=\"admin\"/>" +
    "<OrderStatus orderStatusId=\"XXXX_B2C_CA01_05\" statusId=\"ITEM_APPROVED\" orderId=\"XXXX_B2C_CA01\" orderItemSeqId=\"00001\"/>" +
    "<OrderContactMech orderId=\"XXXX_B2C_CA01\" contactMechPurposeTypeId=\"SHIPPING_LOCATION\" contactMechId=\"9015\"/>" +
    "<OrderContactMech orderId=\"XXXX_B2C_CA01\" contactMechPurposeTypeId=\"ORDER_EMAIL\" contactMechId=\"9026\"/></entity-engine-xml>";
    public String createOrderRelations(String orderId,String partyId){
       String orderrole = "<OrderRole orderId=\"XXXX_B2C_CA01\" partyId=\"DemoCustomer\" roleTypeId=\"PLACING_CUSTOMER\"/>" +
        "<OrderRole orderId=\"XXXX_B2C_CA01\" partyId=\"DemoCustomer\" roleTypeId=\"BILL_TO_CUSTOMER\"/>" +
    "<OrderRole orderId=\"XXXX_B2C_CA01\" partyId=\"Company\" roleTypeId=\"BILL_FROM_VENDOR\"/>" +
    "<OrderRole orderId=\"XXXX_B2C_CA01\" partyId=\"DemoCustomer\" roleTypeId=\"SHIP_TO_CUSTOMER\"/>" +
    "<OrderRole orderId=\"XXXX_B2C_CA01\" partyId=\"DemoCustomer\" roleTypeId=\"END_USER_CUSTOMER\"/>" +
    "<OrderItemShipGroup orderId=\"XXXX_B2C_CA01\" shipGroupSeqId=\"00001\" shipmentMethodTypeId=\"NEXT_DAY\" carrierPartyId=\"UPS\" carrierRoleTypeId=\"CARRIER\" contactMechId=\"9015\" maySplit=\"N\" giftMessage=\"\" isGift=\"N\"/>" +
    "<OrderItemShipGroupAssoc orderId=\"XXXX_B2C_CA01\" orderItemSeqId=\"00001\" shipGroupSeqId=\"00001\" quantity=\"1.0\"/>" +
    "<OrderItemShipGrpInvRes orderId=\"XXXX_B2C_CA01\" shipGroupSeqId=\"00001\" orderItemSeqId=\"00001\" inventoryItemId=\"9001\" reserveOrderEnumId=\"INVRO_FIFO_REC\" quantity=\"1.0\" reservedDatetime=\"2009-12-01 9:00:00.000\" createdDatetime=\"2009-12-01 9:00:00.000\" promisedDatetime=\"2009-12-01 9:00:00.000\" priority=\"2\"/>" +
    "<OrderAdjustment orderAdjustmentId=\"XXXX_B2C_CA01_01\" orderAdjustmentTypeId=\"SHIPPING_CHARGES\" orderId=\"XXXX_B2C_CA01\" orderItemSeqId=\"_NA_\" shipGroupSeqId=\"_NA_\" amount=\"2\" createdDate=\"2009-12-01 9:00:00.000\" createdByUserLogin=\"admin\"/>" +
    "<OrderPaymentPreference orderPaymentPreferenceId=\"XXXX_B2C_CA01_01\" orderId=\"XXXX_B2C_CA01\" paymentMethodTypeId=\"EXT_EBAY\" presentFlag=\"N\" swipedFlag=\"N\" overflowFlag=\"N\" maxAmount=\"14.0\" statusId=\"PAYMENT_NOT_RECEIVED\"/>" +
    "<OrderStatus orderStatusId=\"XXXX_B2C_CA01_01\" statusId=\"ORDER_CREATED\" orderId=\"XXXX_B2C_CA01\" statusDatetime=\"2009-12-01 9:00:00.000\" statusUserLogin=\"admin\"/>" +
    "<OrderStatus orderStatusId=\"XXXX_B2C_CA01_02\" statusId=\"ITEM_CREATED\" orderId=\"XXXX_B2C_CA01\" orderItemSeqId=\"00001\" statusDatetime=\"2009-12-01 9:00:00.000\" statusUserLogin=\"admin\"/>" +
    "<OrderStatus orderStatusId=\"XXXX_B2C_CA01_03\" statusId=\"PAYMENT_NOT_RECEIVED\" orderId=\"XXXX_B2C_CA01\" orderPaymentPreferenceId=\"9000\" statusDatetime=\"2009-12-01 9:00:00.000\" statusUserLogin=\"admin\"/>" +
    "<OrderStatus orderStatusId=\"XXXX_B2C_CA01_04\" statusId=\"ORDER_APPROVED\" orderId=\"XXXX_B2C_CA01\" statusDatetime=\"2009-12-01 9:00:00.000\" statusUserLogin=\"admin\"/>" +
    "<OrderStatus orderStatusId=\"XXXX_B2C_CA01_05\" statusId=\"ITEM_APPROVED\" orderId=\"XXXX_B2C_CA01\" orderItemSeqId=\"00001\"/>" +
    "<OrderContactMech orderId=\"XXXX_B2C_CA01\" contactMechPurposeTypeId=\"SHIPPING_LOCATION\" contactMechId=\"9015\"/>" +
    "<OrderContactMech orderId=\"XXXX_B2C_CA01\" contactMechPurposeTypeId=\"ORDER_EMAIL\" contactMechId=\"9026\"/>";
        return orderrole;
    }
    // *orderId=\"ticket.ticketID\" *orderName=\"ticket.ticketID\" 
    public String createOrderHeader(String ticketid, String grandtotal, String sutotal, String salechannel){
        String orderheader ="<OrderHeader createdBy=\"admin\" createdStamp=\"2012-12-12 16:55:02.663\" createdTxStamp=\"2012-12-12 16:55:02.477\" currencyUom=\"THB\" entryDate=\"2012-12-12 16:55:02.527\" grandTotal=\""+grandtotal+"\" lastUpdatedStamp=\"2012-12-12 16:55:45.237\" lastUpdatedTxStamp=\"2012-12-12 16:55:45.176\" orderDate=\"2012-12-12 16:55:02.527\" orderId=\""+ticketid+"\" orderName=\""+ticketid+"\" orderTypeId=\"SALES_ORDER\" priority=\"2\" productStoreId=\"9000\" remainingSubTotal=\""+sutotal+"\" salesChannelEnumId=\""+salechannel+"\" statusId=\"ORDER_APPROVED\" visitId=\"10342\" webSiteId=\"OrderEntry\"/>";        
        return orderheader;
    }
    public String createOrderItem(String ticketid, String DemoCatalog, String ticketline_product, String ticketline_unit, String ticketline_price, String ticketline_line, String amount, String taxes_name, String adjustmentid, String taxes_rate, String taxes_ID,String status_id){
         //   <OrderItem changeByUserLoginId="admin" correspondingPoId="TangmoPO-01" createdStamp="reciept.datenew" createdTxStamp="2012-12-12 16:55:02.477" isModifiedPrice="N" isPromo="N" itemDescription="Produce by Tangmo" lastUpdatedStamp="2012-12-12 16:55:02.986" lastUpdatedTxStamp="2012-12-12 16:55:02.477" **orderId=""+ticketid+"" *orderItemSeqId="00001" orderItemTypeId="PRODUCT_ORDER_ITEM" *prodCatalogId="DemoCatalog" *productId="ticketline.product" *quantity="ticketline.unit" selectedAmount="0.000000" shipAfterDate="2012-12-12 16:54:10.935" shipBeforeDate="2012-12-13 16:54:14.543" statusId="ITEM_CREATED" *unitListPrice="ticketline.price" *unitPrice="ticketline.price"/>
       
        String orderitem = "<OrderItem changeByUserLoginId=\"admin\" correspondingPoId=\"TangmoPO-01\" createdStamp=\"reciept_datenew\" createdTxStamp=\"2012-12-12 16:55:02.477\" isModifiedPrice=\"N\" isPromo=\"N\" itemDescription=\"Produce by Tangmo\" lastUpdatedStamp=\"2012-12-12 16:55:02.986\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\" orderItemSeqId=\"ItemSeqId\" orderItemTypeId=\"PRODUCT_ORDER_ITEM\" prodCatalogId=\""+DemoCatalog+"\" productId=\""+ticketline_product+"\" quantity=\""+ticketline_unit+"\" selectedAmount=\"0.000000\" shipAfterDate=\"2012-12-12 16:54:10.935\" shipBeforeDate=\"2012-12-13 16:54:14.543\" statusId=\"ITEM_CREATED\" unitListPrice=\""+ticketline_price+"\" unitPrice=\""+ticketline_price+"\"/>";
                orderitem += "<OrderItemShipGroupAssoc createdStamp=\"2012-12-12 16:55:03.132\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:03.132\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\" "
                          + " orderItemSeqId=\""+ticketline_line+"\" quantity=\""+ticketline_unit+"\" shipGroupSeqId=\"00001\"/>";
        //**orderItemSeqId="ticketline.line" อันนี้เพิ่มบรรทัดตามจำนวนสินค้า
                orderitem += "<OrderShipment createdStamp=\"2012-11-03 20:51:10.528\" createdTxStamp=\"2012-11-03 20:51:09.876\" lastUpdatedStamp=\"2012-11-03 20:51:10.528\" lastUpdatedTxStamp=\"2012-11-03 20:51:09.876\" orderId=\""+ticketid+"\" orderItemSeqId=\""+ticketline_line+"\" quantity=\"1.000000\" shipGroupSeqId=\"00001\" shipmentId=\"10002\" shipmentItemSeqId=\"00001\"/>";
                orderitem += "<OrderItemShipGrpInvRes createdDatetime=\"2012-12-12 16:55:04.025\" createdStamp=\"2012-12-12 16:55:04.025\" createdTxStamp=\"2012-12-12 16:55:02.477\" currentPromisedDate=\"2012-12-13 16:54:14.543\" inventoryItemId=\"10031\" lastUpdatedStamp=\"2012-12-12 16:55:04.549\" lastUpdatedTxStamp=\"2012-12-12 16:55:04.538\" orderId=\""+ticketid+"\" orderItemSeqId=\""+ticketline_line+"\" priority=\"2\" promisedDatetime=\"2012-12-13 16:54:14.543\" quantity=\""+ticketline_unit+"\" reserveOrderEnumId=\"INVRO_FIFO_REC\" reservedDatetime=\"2012-12-12 16:55:04.025\" shipGroupSeqId=\"00001\"/>";
        //***orderItemSeqId=\"ticketline.line+1\" เพิ่มตามจำนวนสินค้า
                orderitem += "<OrderAdjustment amount=\""+amount+"\" comments=\""+taxes_name+"\" createdByUserLogin=\"admin\" createdDate=\"2012-12-12 16:55:02.742\" createdStamp=\"2012-12-12 16:55:03.167\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:03.167\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderAdjustmentId=\""+adjustmentid+"\" orderAdjustmentTypeId=\"SALES_TAX\" orderId=\""+ticketid+"\" orderItemSeqId=\""+ticketline_line+"\" primaryGeoId=\"_NA_\" shipGroupSeqId=\"00001\" sourcePercentage=\""+taxes_rate+"\" taxAuthGeoId=\"_NA_\" taxAuthPartyId=\""+taxes_ID+"\" taxAuthorityRateSeqId=\"9000\"/>";
        //*orderAdjustmentId=\"10072\"Running number
                    orderitem += "<OrderStatus createdStamp=\"2012-12-12 16:55:02.998\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:02.998\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\" orderItemSeqId=\""+ticketline_line+"\" orderStatusId=\""+status_id+"\" statusDatetime=\"2012-12-12 16:55:02.527\" statusId=\"ITEM_CREATED\" statusUserLogin=\"admin\"/>";
         System.out.println(orderitem);
                    return orderitem;
    }
    
    public String createOrderAll(String ticketid, String custimer_ID, String people_ID, String max_amount, String note_id, String receipt_money, String people_name, String texes_rateorder, String texes_ratecascade, String texes_paymentID, String texes_custcategory, String customers_searchkey, String customers_taxID, String customers_name, String ticketlines_attribute, String texes_validfrom, String texes_category){
        String all = "";
      try{
//**orderItemSeqId="ticketline.line" อันนี้เพิ่มบรรทัดตามจำนวนสินค้า
//-------------------------------------------------------------------------------------------------------------------------------
    all +="<OrderRole createdStamp=\"2012-12-12 16:55:03.216\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:03.216\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\" partyId=\""+custimer_ID+"\" roleTypeId=\"PLACING_CUSTOMER\"/>";
    all +="<OrderRole createdStamp=\"2012-12-12 16:55:03.223\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:03.223\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\" partyId=\""+custimer_ID+"\" roleTypeId=\"BILL_TO_CUSTOMER\"/>";
    all +="<OrderRole createdStamp=\"2012-12-12 16:55:03.236\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:03.236\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\" partyId=\""+people_ID+"\" roleTypeId=\"BILL_FROM_VENDOR\"/>";
    all +="<OrderRole createdStamp=\"2012-12-12 16:55:03.239\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:03.239\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\" partyId=\""+custimer_ID+"\" roleTypeId=\"SHIP_TO_CUSTOMER\"/>";
    all +="<OrderRole createdStamp=\"2012-12-12 16:55:03.241\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:03.241\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\" partyId=\""+custimer_ID+"\" roleTypeId=\"END_USER_CUSTOMER\"/>";
//--------------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------------
    all += "<OrderStatus createdStamp=\"2012-12-12 16:55:02.868\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:02.868\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\" *orderStatusId=\""+ticketid+"01"+"\" statusDatetime=\"2012-12-12 16:55:02.527\" statusId=\"ORDER_CREATED\" statusUserLogin=\"admin\"/>";
//    all += "<OrderStatus createdStamp=\"2012-12-12 16:55:02.957\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:02.957\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" **orderId=\""+ticketid+"\" orderItemSeqId=\"00002\" *orderStatusId=\"10198\" statusDatetime=\"2012-12-12 16:55:02.527\" statusId=\"ITEM_CREATED\" statusUserLogin=\"admin\"/>";
//    all += "<OrderStatus createdStamp=\"2012-12-12 16:55:02.998\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:02.998\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" **orderId=\""+ticketid+"\" orderItemSeqId=\"00001\" *orderStatusId=\"10199\" statusDatetime=\"2012-12-12 16:55:02.527\" statusId=\"ITEM_CREATED\" statusUserLogin=\"admin\"/>";
    all += "<OrderStatus createdStamp=\"2012-12-12 16:55:03.373\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:03.373\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\" orderPaymentPreferenceId=\"10062\" *orderStatusId=\""+ticketid+"02"+"\" statusDatetime=\"2012-12-12 16:55:03.373\" statusId=\"PAYMENT_NOT_RECEIVED\" statusUserLogin=\"admin\"/>";
    all += "<OrderStatus createdStamp=\"2012-12-12 16:55:43.401\" createdTxStamp=\"2012-12-12 16:55:43.348\" lastUpdatedStamp=\"2012-12-12 16:55:43.401\" lastUpdatedTxStamp=\"2012-12-12 16:55:43.348\" orderId=\""+ticketid+"\" orderPaymentPreferenceId=\"10063\" *orderStatusId=\""+ticketid+"03"+"\" statusDatetime=\"2012-12-12 16:55:43.401\" statusId=\"PAYMENT_RECEIVED\" statusUserLogin=\"admin\"/>";
    all += "<OrderStatus createdStamp=\"2012-12-12 16:55:45.142\" createdTxStamp=\"2012-12-12 16:55:45.119\" lastUpdatedStamp=\"2012-12-12 16:55:45.142\" lastUpdatedTxStamp=\"2012-12-12 16:55:45.119\" orderId=\""+ticketid+"\" orderPaymentPreferenceId=\"10062\" *orderStatusId=\""+ticketid+"04"+"\" statusDatetime=\"2012-12-12 16:55:45.142\" statusId=\"PAYMENT_CANCELLED\" statusUserLogin=\"admin\"/>";
    all += "<OrderStatus createdStamp=\"2012-12-12 16:55:45.299\" createdTxStamp=\"2012-12-12 16:55:45.176\" lastUpdatedStamp=\"2012-12-12 16:55:45.299\" lastUpdatedTxStamp=\"2012-12-12 16:55:45.176\" orderId=\""+ticketid+"\" orderStatusId=\""+ticketid+"05"+"\" statusDatetime=\"2012-12-12 16:55:45.237\" statusId=\"ORDER_APPROVED\" statusUserLogin=\"admin\"/>";
//*orderStatusId=\"ต้องห้ามซ้ำ Running number\"
//----------------------------------------------------------------------------------------------------------------------------------
//    all += "<OrderAdjustment *amount=\"2.599\" *comments=\"taxes.name\" createdByUserLogin=\"admin\" createdDate=\"2012-12-12 16:55:02.742\" createdStamp=\"2012-12-12 16:55:03.167\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:03.167\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" *orderAdjustmentId=\"10072\" orderAdjustmentTypeId=\"SALES_TAX\" **orderId=\""+ticketid+"\" orderItemSeqId=\"00001\" primaryGeoId=\"_NA_\" shipGroupSeqId=\"00001\" *sourcePercentage=\"taxes.rate\" taxAuthGeoId=\"_NA_\" taxAuthPartyId=\"taxes.ID\" taxAuthorityRateSeqId=\"9000\"/>";
////*orderAdjustmentId=\"10072\"Running number
//----------------------------------------------------------------------------------------------------------------------------------
    all += "<OrderContactMech contactMechId=\"10000\" contactMechPurposeTypeId=\"SHIPPING_LOCATION\" createdStamp=\"2012-12-12 16:55:03.031\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:03.031\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" orderId=\""+ticketid+"\"/>";
//*contactMechId=\"IDที่อยู่ของลูกค้า\"
//----------------------------------------------------------------------------------------------------------------------------------
//    all += "<OrderHeader createdBy=\"admin\" createdStamp=\"2012-12-12 16:55:02.663\" createdTxStamp=\"2012-12-12 16:55:02.477\" currencyUom=\"THB\" entryDate=\"2012-12-12 16:55:02.527\" ****grandTotal=\"422.40\" lastUpdatedStamp=\"2012-12-12 16:55:45.237\" lastUpdatedTxStamp=\"2012-12-12 16:55:45.176\" orderDate=\"2012-12-12 16:55:02.527\" *orderId=\""+ticketid+"\" *orderName=\"ticket.ticketID\" orderTypeId=\"SALES_ORDER\" priority=\"2\" *productStoreId=\"9000\" ****remainingSubTotal=\"419.80\" salesChannelEnumId=\"POS_SALES_CHANNEL\" statusId=\"ORDER_APPROVED\" visitId=\"10342\" webSiteId=\"OrderEntry\"/>";
//****grandTotal=\"422.40\"/ราคารวมภาษีแล้ว ตัวopen bravo ไม่มีรวมราคาตรงนี้
//****remainingSubTotal=\"419.80\"/ราคาก่อนรวมภาษี ตัวopen bravo ไม่มีรวมราคาตรงนี้
//----------------------------------------------------------------------------------------------------------------------------------
    all += "<OrderItemShipGroup carrierPartyId=\"USPS\" carrierRoleTypeId=\"CARRIER\" contactMechId=\"10000\" createdStamp=\"2012-12-12 16:55:03.086\" createdTxStamp=\"2012-12-12 16:55:02.477\" giftMessage=\"\" isGift=\"N\" lastUpdatedStamp=\"2012-12-12 16:55:03.086\" lastUpdatedTxStamp=\"2012-12-12 16:55:02.477\" maySplit=\"N\" orderId=\""+ticketid+"\" shipAfterDate=\"2012-12-12 16:54:10.935\" shipByDate=\"2012-12-13 16:54:14.543\" shipGroupSeqId=\"00001\" shipmentMethodTypeId=\"EXPRESS\" shippingInstructions=\"\"/>";
//*contactMechId=\"IDที่อยู่ของลูกค้า\"
////----------------------------------------------------------------------------------------------------------------------------------
//    all += "<OrderItemShipGrpInvRes createdDatetime=\"2012-12-12 16:55:04.025\" createdStamp=\"2012-12-12 16:55:04.025\" createdTxStamp=\"2012-12-12 16:55:02.477\" currentPromisedDate=\"2012-12-13 16:54:14.543\" inventoryItemId=\"10031\" lastUpdatedStamp=\"2012-12-12 16:55:04.549\" lastUpdatedTxStamp=\"2012-12-12 16:55:04.538\" **orderId=\""+ticketid+"\" *orderItemSeqId=\"ticketline.line\" priority=\"2\" promisedDatetime=\"2012-12-13 16:54:14.543\" *quantity=\"ticketline.unit\" reserveOrderEnumId=\"INVRO_FIFO_REC\" reservedDatetime=\"2012-12-12 16:55:04.025\" shipGroupSeqId=\"00001\"/>";
//    all += "<OrderItemShipGrpInvRes createdDatetime=\"2012-12-12 16:55:04.224\" createdStamp=\"2012-12-12 16:55:04.224\" createdTxStamp=\"2012-12-12 16:55:02.477\" currentPromisedDate=\"2012-12-13 16:54:14.543\" inventoryItemId=\"10030\" lastUpdatedStamp=\"2012-12-12 16:55:04.565\" lastUpdatedTxStamp=\"2012-12-12 16:55:04.538\" **orderId=\""+ticketid+"\" orderItemSeqId=\"ticketline.line+1\" priority=\"2\" promisedDatetime=\"2012-12-13 16:54:14.543\" *quantity=\"ticketline.unit\" reserveOrderEnumId=\"INVRO_FIFO_REC\" reservedDatetime=\"2012-12-12 16:55:04.224\" shipGroupSeqId=\"00001\"/>";

//----------------------------------------------------------------------------------------------------------------------------------
    all += "<OrderPaymentPreference createdByUserLogin=\"admin\" createdDate=\"2012-12-12 16:55:02.851\" createdStamp=\"2012-12-12 16:55:03.262\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2012-12-12 16:55:45.12\" lastUpdatedTxStamp=\"2012-12-12 16:55:45.119\" maxAmount=\""+max_amount+"\" orderId=\""+ticketid+"\" orderPaymentPreferenceId=\"10062\" overflowFlag=\"N\" paymentMethodTypeId=\"EXT_OFFLINE\" presentFlag=\"N\" shipGroupSeqId=\"00001\" statusId=\"PAYMENT_CANCELLED\" swipedFlag=\"N\"/>";
    all += "<OrderPaymentPreference createdByUserLogin=\"admin\" createdDate=\"2012-12-12 16:55:43.348\" createdStamp=\"2012-12-12 16:55:43.348\" createdTxStamp=\"2012-12-12 16:55:43.348\" lastUpdatedStamp=\"2012-12-12 16:55:43.348\" lastUpdatedTxStamp=\"2012-12-12 16:55:43.348\" maxAmount=\""+max_amount+"\" orderId=\""+ticketid+"\" orderPaymentPreferenceId=\"10063\" paymentMethodTypeId=\"CASH\" statusId=\"PAYMENT_RECEIVED\"/>";
//*maxAmount=\"422.40\"คือgrandTotal
//**orderPaymentPreferenceId=\"10062\"-running number
//----------------------------------------------------------------------------------------------------------------------------------

//****OrderHeaderNote เอาไว้เก็บส่วนที่นอกเหนือจากตารางที่ Ofbiz มี จะเชื่อมระหว่างOrderID กับ NoteID ซึ่งจะอ้างกับตารางNoteData
//NoteID=running number

//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:48:01.28\" createdTxStamp=\"2012-12-13 10:48:01.0\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:48:01.28\" lastUpdatedTxStamp=\"2012-12-13 10:48:01.0\" noteId=\""+note_id+"01"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"02"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"03"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"04"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"05"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"06"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"07"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"08"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"09"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"10"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"11"+"\" orderId=\""+ticketid+"\"/>";
//all += "<OrderHeaderNote createdStamp=\"2012-12-13 10:52:48.188\" createdTxStamp=\"2012-12-13 10:52:48.181\" internalNote=\"Y\" lastUpdatedStamp=\"2012-12-13 10:52:48.188\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteId=\""+note_id+"12"+"\" orderId=\""+ticketid+"\"/>";
//
////---------------------------------------------------------------------------------------------------------------------------------
//
//all += "<NoteData createdStamp=\"2012-12-13 10:48:01.261\" createdTxStamp=\"2012-12-13 10:48:01.0\" lastUpdatedStamp=\"2012-12-13 10:48:01.261\" lastUpdatedTxStamp=\"2012-12-13 10:48:01.0\" noteDateTime=\"2012-12-13 10:48:01.26\" noteId=\""+note_id+"01"+"\" noteInfo=\""+receipt_money+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"02"+"\" noteInfo=\""+people_name+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"03"+"\" noteInfo=\""+customers_searchkey+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"04"+"\" noteInfo=\""+customers_taxID+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"05"+"\" noteInfo=\""+customers_name+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"06"+"\" noteInfo=\""+ticketlines_attribute+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"07"+"\" noteInfo=\""+texes_validfrom+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"08"+"\" noteInfo=\""+texes_category+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"09"+"\" noteInfo=\""+texes_custcategory+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"10"+"\" noteInfo=\""+texes_paymentID+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"11"+"\" noteInfo=\""+texes_ratecascade+"\" noteParty=\"admin\"/>";
//all += "<NoteData createdStamp=\"2012-12-13 10:52:48.184\" createdTxStamp=\"2012-12-13 10:52:48.181\" lastUpdatedStamp=\"2012-12-13 10:52:48.184\" lastUpdatedTxStamp=\"2012-12-13 10:52:48.181\" noteDateTime=\"2012-12-13 10:52:48.184\" noteId=\""+note_id+"12"+"\" noteInfo=\""+texes_rateorder+"\" noteParty=\"admin\"/>";
////เพิ่มบรรทัดในการเก็บ
//NoteData เอาไว้เก็บ receipt.money/people.name/customers.searchkey/customers.taxID/customers.name/ticketlines.attribute/texes.validfrom/
//texes.category/texes.custcategory/texes.paymentID/texes.ratecascade/texes.rateorder
//---------------------------------------------------------------------------------------------------------------------------------
      }catch(Exception e){ System.out.println(" Error =============== "+e.getMessage()); }
       return all;
    }
    
    public String syncData(String fulltext) throws AxisFault {

        Options options = new Options();
        options.setTo(new EndpointReference(toEpr));
        options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

        options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
        options.setAction("entityImport");
        ServiceClient sender = new ServiceClient();
       // sender.engageModule(Constants.MODULE_ADDRESSING);
        sender.setOptions(options);
        System.out.println(createPayLoad(fulltext).toString());
        OMElement result = sender.sendReceive(createPayLoad(fulltext));

        try {
            XMLStreamWriter writer = XMLOutputFactory.newInstance()
                    .createXMLStreamWriter(System.out);
            result.serialize(writer);
            writer.flush();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();
        }
        return "sucsess";
    }
     static  OMFactory fac = OMAbstractFactory.getOMFactory();
     static  OMNamespace omNs = fac.createOMNamespace(
                "http://ofbiz.apache.org/service/", "ns2");
    static  OMFactory fac2 = OMAbstractFactory.getOMFactory();
    static  OMNamespace omNs2 = fac.createOMNamespace(
                "http://schemas.xmlsoap.org/soap/envelope/", "soapenv");

   private  OMElement createPayLoad(String fulltext) {
      
      OMElement soap = fac.createOMElement("Envelope", omNs2);
      OMElement body = fac.createOMElement("Body", omNs2);  
      soap.addChild(body);
      OMElement findPartiesById = fac.createOMElement("entityImport", omNs);
      OMElement mapMap = fac.createOMElement("map-Map", null);

      findPartiesById.addChild(mapMap);

      mapMap.addChild(createMapEntry("fulltext", fulltext));
      mapMap.addChild(createMapEntry("login.username", "admin"));
      mapMap.addChild(createMapEntry("login.password", "ofbiz"));
      body.addChild(findPartiesById);
      return soap;
   }

   private  OMElement createMapEntry(String key, String val) {

      OMElement mapEntry = fac.createOMElement("map-Entry",null);

      // create the key
      OMElement mapKey = fac.createOMElement("map-Key",null);
      OMElement keyElement = fac.createOMElement("std-String",null);
      OMAttribute keyAttribute = fac.createOMAttribute("value", null, key);

      mapKey.addChild(keyElement);
      keyElement.addAttribute(keyAttribute);

      // create the value
      OMElement mapValue = fac.createOMElement("map-Value", null);
      OMElement valElement = fac.createOMElement("std-String", null);
      OMAttribute valAttribute = fac.createOMAttribute("value", null, val);

      mapValue.addChild(valElement);
      valElement.addAttribute(valAttribute);

      // attach to map-Entry
      mapEntry.addChild(mapKey);
      mapEntry.addChild(mapValue);

      return mapEntry;
   }//salesChannel
}
