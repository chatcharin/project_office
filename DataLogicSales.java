//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.forms;

import com.openbravo.pos.ticket.CategoryInfo;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;
import com.openbravo.data.model.Field;
import com.openbravo.data.model.Row;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.inventory.AttributeSetInfo;
import com.openbravo.pos.inventory.TaxCustCategoryInfo;
import com.openbravo.pos.inventory.LocationInfo;
import com.openbravo.pos.inventory.MovementReason;
import com.openbravo.pos.inventory.TaxCategoryInfo;
import com.openbravo.pos.mant.FloorsInfo;
import com.openbravo.pos.payment.PaymentInfo;
import com.openbravo.pos.payment.PaymentInfoTicket;
import com.openbravo.pos.ticket.FindTicketsInfo;
import com.openbravo.pos.ticket.TicketTaxInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.axis2.AxisFault;

/**
 *
 * @author adrianromero
 */
public class DataLogicSales extends BeanFactoryDataSingle {

    protected Session s;

    protected Datas[] auxiliarDatas;
    protected Datas[] stockdiaryDatas;
    // protected Datas[] productcatDatas;
    protected Datas[] paymenttabledatas;
    protected Datas[] stockdatas;

    protected Row productsRow;

    /** Creates a new instance of SentenceContainerGeneric */
    public DataLogicSales() {
        stockdiaryDatas = new Datas[] {Datas.STRING, Datas.TIMESTAMP, Datas.INT, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE};
        paymenttabledatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.TIMESTAMP, Datas.STRING, Datas.STRING, Datas.DOUBLE};
        stockdatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE};
        auxiliarDatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING};

        productsRow = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING, true, true, true),
                new Field(AppLocal.getIntString("label.prodbarcode"), Datas.STRING, Formats.STRING, false, true, true),
                new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING, true, true, true),
                new Field("ISCOM", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("ISSCALE", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field(AppLocal.getIntString("label.prodpricebuy"), Datas.DOUBLE, Formats.CURRENCY, false, true, true),
                new Field(AppLocal.getIntString("label.prodpricesell"), Datas.DOUBLE, Formats.CURRENCY, false, true, true),
                new Field(AppLocal.getIntString("label.prodcategory"), Datas.STRING, Formats.STRING, false, false, true),
                new Field(AppLocal.getIntString("label.taxcategory"), Datas.STRING, Formats.STRING, false, false, true),
                new Field(AppLocal.getIntString("label.attributeset"), Datas.STRING, Formats.STRING, false, false, true),
                new Field("IMAGE", Datas.IMAGE, Formats.NULL),
                new Field("STOCKCOST", Datas.DOUBLE, Formats.CURRENCY),
                new Field("STOCKVOLUME", Datas.DOUBLE, Formats.DOUBLE),
                new Field("ISCATALOG", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("CATORDER", Datas.INT, Formats.INT),
                new Field("PROPERTIES", Datas.BYTES, Formats.NULL));
    }

    public void init(Session s){
        this.s = s;
    }

    public final Row getProductsRow() {
        return productsRow;
    }

    // Utilidades de productos
    public final ProductInfoExt getProductInfo(String id) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s
            , "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES " +
              "FROM PRODUCTS WHERE ID = ?"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).find(id);
    }
    public final ProductInfoExt getProductInfoByCode(String sCode) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s
            , "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES " +
              "FROM PRODUCTS WHERE CODE = ?"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).find(sCode);
    }
    public final ProductInfoExt getProductInfoByReference(String sReference) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s
            , "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES " +
              "FROM PRODUCTS WHERE REFERENCE = ?"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).find(sReference);
    }

    // Catalogo de productos
    public final List<CategoryInfo> getRootCategories() throws BasicException {
        return new PreparedSentence(s
            , "SELECT ID, NAME, IMAGE FROM CATEGORIES WHERE PARENTID IS NULL ORDER BY NAME"
            , null
            , CategoryInfo.getSerializerRead()).list();
    }
    public final List<CategoryInfo> getSubcategories(String category) throws BasicException  {
        return new PreparedSentence(s
            , "SELECT ID, NAME, IMAGE FROM CATEGORIES WHERE PARENTID = ? ORDER BY NAME"
            , SerializerWriteString.INSTANCE
            , CategoryInfo.getSerializerRead()).list(category);
    }
    public List<ProductInfoExt> getProductCatalog(String category) throws BasicException  {
        return new PreparedSentence(s
            , "SELECT P.ID, P.REFERENCE, P.CODE, P.NAME, P.ISCOM, P.ISSCALE, P.PRICEBUY, P.PRICESELL, P.TAXCAT, P.CATEGORY, P.ATTRIBUTESET_ID, P.IMAGE, P.ATTRIBUTES " +
              "FROM PRODUCTS P, PRODUCTS_CAT O WHERE P.ID = O.PRODUCT AND P.CATEGORY = ? " +
              "ORDER BY O.CATORDER, P.NAME"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).list(category);
    }
    public List<ProductInfoExt> getProductComments(String id) throws BasicException {
        return new PreparedSentence(s
            , "SELECT P.ID, P.REFERENCE, P.CODE, P.NAME, P.ISCOM, P.ISSCALE, P.PRICEBUY, P.PRICESELL, P.TAXCAT, P.CATEGORY, P.ATTRIBUTESET_ID, P.IMAGE, P.ATTRIBUTES " +
              "FROM PRODUCTS P, PRODUCTS_CAT O, PRODUCTS_COM M WHERE P.ID = O.PRODUCT AND P.ID = M.PRODUCT2 AND M.PRODUCT = ? " +
              "AND P.ISCOM = " + s.DB.TRUE() + " " +
              "ORDER BY O.CATORDER, P.NAME"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).list(id);
    }
  
    // Products list
    public final SentenceList getProductList() {
        return new StaticSentence(s
            , new QBFBuilder(
              "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES " +
              "FROM PRODUCTS WHERE ?(QBF_FILTER) ORDER BY REFERENCE", new String[] {"NAME", "PRICEBUY", "PRICESELL", "CATEGORY", "CODE"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING})
            , ProductInfoExt.getSerializerRead());
    }
    
    // Products list
    public SentenceList getProductListNormal() {
        return new StaticSentence(s
            , new QBFBuilder(
              "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES " +
              "FROM PRODUCTS WHERE ISCOM = " + s.DB.FALSE() + " AND ?(QBF_FILTER) ORDER BY REFERENCE", new String[] {"NAME", "PRICEBUY", "PRICESELL", "CATEGORY", "CODE"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING})
            , ProductInfoExt.getSerializerRead());
    }
    
    //Auxiliar list for a filter
    public SentenceList getProductListAuxiliar() {
         return new StaticSentence(s
            , new QBFBuilder(
              "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES " +
              "FROM PRODUCTS WHERE ISCOM = " + s.DB.TRUE() + " AND ?(QBF_FILTER) ORDER BY REFERENCE", new String[] {"NAME", "PRICEBUY", "PRICESELL", "CATEGORY", "CODE"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING})
            , ProductInfoExt.getSerializerRead());
    }
    
    //Tickets and Receipt list
    public SentenceList getTicketsList() {
         return new StaticSentence(s
            , new QBFBuilder(
            "SELECT T.TICKETID, T.TICKETTYPE, R.DATENEW, P.NAME, C.NAME, SUM(PM.TOTAL) "+ 
            "FROM RECEIPTS R JOIN TICKETS T ON R.ID = T.ID LEFT OUTER JOIN PAYMENTS PM ON R.ID = PM.RECEIPT LEFT OUTER JOIN CUSTOMERS C ON C.ID = T.CUSTOMER LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID " +
            "WHERE ?(QBF_FILTER) GROUP BY T.ID, T.TICKETID, T.TICKETTYPE, R.DATENEW, P.NAME, C.NAME ORDER BY R.DATENEW DESC, T.TICKETID", new String[] {"T.TICKETID", "T.TICKETTYPE", "PM.TOTAL", "R.DATENEW", "R.DATENEW", "P.NAME", "C.NAME"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.INT, Datas.OBJECT, Datas.INT, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.TIMESTAMP, Datas.OBJECT, Datas.TIMESTAMP, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING})
            , new SerializerReadClass(FindTicketsInfo.class));
    }
    
    //User list
    public final SentenceList getUserList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM PEOPLE ORDER BY NAME"
            , null
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(
                        dr.getString(1), 
                        dr.getString(2));
            }});
    }
   
    // Listados para combo
    public final SentenceList getTaxList() {
        return new StaticSentence(s
            , "SELECT ID, NAME, CATEGORY, VALIDFROM, CUSTCATEGORY, PARENTID, RATE, RATECASCADE, RATEORDER FROM TAXES ORDER BY NAME"
            , null
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new TaxInfo(
                        dr.getString(1), 
                        dr.getString(2),
                        dr.getString(3),
                        dr.getTimestamp(4),
                        dr.getString(5),
                        dr.getString(6),
                        dr.getDouble(7).doubleValue(),
                        dr.getBoolean(8).booleanValue(),
                        dr.getInt(9));
            }});
    }
    public final SentenceList getCategoriesList() {
        return new StaticSentence(s
            , "SELECT ID, NAME, IMAGE FROM CATEGORIES ORDER BY NAME"
            , null
            , CategoryInfo.getSerializerRead());
    }
    public final SentenceList getTaxCustCategoriesList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM TAXCUSTCATEGORIES ORDER BY NAME"
            , null
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new TaxCustCategoryInfo(dr.getString(1), dr.getString(2));
            }});
    }
    public final SentenceList getTaxCategoriesList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM TAXCATEGORIES ORDER BY NAME"
            , null
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(dr.getString(1), dr.getString(2));
            }});
    }
    public final SentenceList getAttributeSetList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM ATTRIBUTESET ORDER BY NAME"
            , null
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new AttributeSetInfo(dr.getString(1), dr.getString(2));
            }});
    }
    public final SentenceList getLocationsList() {
        return new StaticSentence(s
            , "SELECT ID, NAME, ADDRESS FROM LOCATIONS ORDER BY NAME"
            , null
            , new SerializerReadClass(LocationInfo.class));
    }
    public final SentenceList getFloorsList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM FLOORS ORDER BY NAME"
            , null
            , new SerializerReadClass(FloorsInfo.class));
    }

    public CustomerInfoExt findCustomerExt(String card) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s
                , "SELECT ID, TAXID, SEARCHKEY, NAME, CARD, TAXCATEGORY, NOTES, MAXDEBT, VISIBLE, CURDATE, CURDEBT" +
                  ", FIRSTNAME, LASTNAME, EMAIL, PHONE, PHONE2, FAX" +
                  ", ADDRESS, ADDRESS2, POSTAL, CITY, REGION, COUNTRY" +
                  " FROM CUSTOMERS WHERE CARD = ? AND VISIBLE = " + s.DB.TRUE()
                , SerializerWriteString.INSTANCE
                , new CustomerExtRead()).find(card);
    }

    public CustomerInfoExt loadCustomerExt(String id) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s
                , "SELECT ID, TAXID, SEARCHKEY, NAME, CARD, TAXCATEGORY, NOTES, MAXDEBT, VISIBLE, CURDATE, CURDEBT" +
                  ", FIRSTNAME, LASTNAME, EMAIL, PHONE, PHONE2, FAX" +
                  ", ADDRESS, ADDRESS2, POSTAL, CITY, REGION, COUNTRY" +
                " FROM CUSTOMERS WHERE ID = ?"
                , SerializerWriteString.INSTANCE
                , new CustomerExtRead()).find(id);
    }

    public final boolean isCashActive(String id) throws BasicException {

        return new PreparedSentence(s,
                "SELECT MONEY FROM CLOSEDCASH WHERE DATEEND IS NULL AND MONEY = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE).find(id)
            != null;
    }

    public final TicketInfo loadTicket(final int tickettype, final int ticketid) throws BasicException {
        TicketInfo ticket = (TicketInfo) new PreparedSentence(s
                , "SELECT T.ID, T.TICKETTYPE, T.TICKETID, R.DATENEW, R.MONEY, R.ATTRIBUTES, P.ID, P.NAME, T.CUSTOMER FROM RECEIPTS R JOIN TICKETS T ON R.ID = T.ID LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID WHERE T.TICKETTYPE = ? AND T.TICKETID = ?"
                , SerializerWriteParams.INSTANCE
                , new SerializerReadClass(TicketInfo.class))
                .find(new DataParams() { public void writeValues() throws BasicException {
                    setInt(1, tickettype);
                    setInt(2, ticketid);
                }});
        if (ticket != null) {

            String customerid = ticket.getCustomerId();
            ticket.setCustomer(customerid == null
                    ? null
                    : loadCustomerExt(customerid));

            ticket.setLines(new PreparedSentence(s
                , "SELECT L.TICKET, L.LINE, L.PRODUCT, L.ATTRIBUTESETINSTANCE_ID, L.UNITS, L.PRICE, T.ID, T.NAME, T.CATEGORY, T.VALIDFROM, T.CUSTCATEGORY, T.PARENTID, T.RATE, T.RATECASCADE, T.RATEORDER, L.ATTRIBUTES " +
                  "FROM TICKETLINES L, TAXES T WHERE L.TAXID = T.ID AND L.TICKET = ? ORDER BY L.LINE"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(TicketLineInfo.class)).list(ticket.getId()));
            ticket.setPayments(new PreparedSentence(s
                , "SELECT PAYMENT, TOTAL, TRANSID FROM PAYMENTS WHERE RECEIPT = ?"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentInfoTicket.class)).list(ticket.getId()));
        }
        return ticket;
    }

    public final void saveTicket(final TicketInfo ticket, final String location) throws BasicException {

        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {

                // Set Receipt Id
                if (ticket.getTicketId() == 0) {
                    switch (ticket.getTicketType()) {
                        case TicketInfo.RECEIPT_NORMAL:
                            ticket.setTicketId(getNextTicketIndex().intValue());
                            break;
                        case TicketInfo.RECEIPT_REFUND:
                            ticket.setTicketId(getNextTicketRefundIndex().intValue());
                            break;
                        case TicketInfo.RECEIPT_PAYMENT:
                            ticket.setTicketId(getNextTicketPaymentIndex().intValue());
                            break;
                        default:
                            throw new BasicException();
                    }
                }

                // new receipt
                new PreparedSentence(s
                    , "INSERT INTO RECEIPTS (ID, MONEY, DATENEW, ATTRIBUTES) VALUES (?, ?, ?, ?)"
                    , SerializerWriteParams.INSTANCE
                    ).exec(new DataParams() { public void writeValues() throws BasicException {
                        setString(1, ticket.getId());
                        setString(2, ticket.getActiveCash());
                        setTimestamp(3, ticket.getDate());
                        try {
                            ByteArrayOutputStream o = new ByteArrayOutputStream();
                            ticket.getProperties().storeToXML(o, AppLocal.APP_NAME, "UTF-8");
                            setBytes(4, o.toByteArray());
                        } catch (IOException e) {
                            setBytes(4, null);
                        }
                    }});

                // new ticket
                new PreparedSentence(s
                    , "INSERT INTO TICKETS (ID, TICKETTYPE, TICKETID, PERSON, CUSTOMER) VALUES (?, ?, ?, ?, ?)"
                    , SerializerWriteParams.INSTANCE
                    ).exec(new DataParams() { public void writeValues() throws BasicException {
                        setString(1, ticket.getId());
                        setInt(2, ticket.getTicketType());
                        setInt(3, ticket.getTicketId());
                        setString(4, ticket.getUser().getId());
                        setString(5, ticket.getCustomerId());
                    }});

                SentenceExec ticketlineinsert = new PreparedSentence(s
                    , "INSERT INTO TICKETLINES (TICKET, LINE, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, TAXID, ATTRIBUTES) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    , SerializerWriteBuilder.INSTANCE);

                for (TicketLineInfo l : ticket.getLines()) {
                    ticketlineinsert.exec(l);
                    if (l.getProductID() != null)  {
                        // update the stock
                        getStockDiaryInsert().exec(new Object[] {
                            UUID.randomUUID().toString(),
                            ticket.getDate(),
                            l.getMultiply() < 0.0
                                ? MovementReason.IN_REFUND.getKey()
                                : MovementReason.OUT_SALE.getKey(),
                            location,
                            l.getProductID(),
                            l.getProductAttSetInstId(),
                            new Double(-l.getMultiply()),
                            new Double(l.getPrice())
                        });
                    }
                }

                SentenceExec paymentinsert = new PreparedSentence(s
                    , "INSERT INTO PAYMENTS (ID, RECEIPT, PAYMENT, TOTAL, TRANSID, RETURNMSG) VALUES (?, ?, ?, ?, ?, ?)"
                    , SerializerWriteParams.INSTANCE);
                for (final PaymentInfo p : ticket.getPayments()) {
                    paymentinsert.exec(new DataParams() { public void writeValues() throws BasicException {
                        setString(1, UUID.randomUUID().toString());
                        setString(2, ticket.getId());
                        setString(3, p.getName());
                        setDouble(4, p.getTotal());
                        setString(5, ticket.getTransactionID());
                        setBytes(6, (byte[]) Formats.BYTEA.parseValue(ticket.getReturnMessage()));
                    }});

                    if ("debt".equals(p.getName()) || "debtpaid".equals(p.getName())) {

                        // udate customer fields...
                        ticket.getCustomer().updateCurDebt(p.getTotal(), ticket.getDate());

                        // save customer fields...
                        getDebtUpdate().exec(new DataParams() { public void writeValues() throws BasicException {
                            setDouble(1, ticket.getCustomer().getCurdebt());
                            setTimestamp(2, ticket.getCustomer().getCurdate());
                            setString(3, ticket.getCustomer().getId());
                        }});
                    }
                }

                SentenceExec taxlinesinsert = new PreparedSentence(s
                        , "INSERT INTO TAXLINES (ID, RECEIPT, TAXID, BASE, AMOUNT)  VALUES (?, ?, ?, ?, ?)"
                        , SerializerWriteParams.INSTANCE);
                if (ticket.getTaxes() != null) {
                    for (final TicketTaxInfo tickettax: ticket.getTaxes()) {
                        taxlinesinsert.exec(new DataParams() { public void writeValues() throws BasicException {
                            setString(1, UUID.randomUUID().toString());
                            setString(2, ticket.getId());
                            setString(3, tickettax.getTaxInfo().getId());
                            setDouble(4, tickettax.getSubTotal());
                            setDouble(5, tickettax.getTax());
                        }});
                    }
                }

                return null;
            }
        };
        t.execute();
      try {
        String fulltext_xml = "<entity-engine-xml>";
        pf("<entity-engine-xml>");   
        fulltext_xml += "<OrderHeader createdBy=\"admin\" createdStamp=\"2012-12-12 16:55:02.663\" createdTxStamp=\"2012-12-12 16:55:02.477\" currencyUom=\"THB\" entryDate=\"2012-12-12 16:55:02.527\" grandTotal=\""+String.valueOf(ticket.getTotal())+"\" lastUpdatedStamp=\"2013-01-18 19:25:38.721\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" orderDate=\"2009-08-13 17:45:50.419\" orderId=\"mc-"+ticket.printId()+"\" orderName=\"mc-"+ticket.printId()+"\" orderTypeId=\"SALES_ORDER\" priority=\"2\" productStoreId=\"9000\" remainingSubTotal=\""+String.valueOf(ticket.getTotal())+"\" salesChannelEnumId=\"POS_SALES_CHANNEL\" statusId=\"ORDER_APPROVED\" visitId=\"10342\" webSiteId=\"OrderEntry\"/>";
        pf("<OrderHeader createdBy=\"admin\" createdStamp=\"2012-12-12 16:55:02.663\" createdTxStamp=\"2012-12-12 16:55:02.477\" currencyUom=\"THB\" entryDate=\"2012-12-12 16:55:02.527\" grandTotal=\""+String.valueOf(ticket.getTotal())+"\" lastUpdatedStamp=\"2013-01-18 19:25:38.721\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" orderDate=\"2009-08-13 17:45:50.419\" orderId=\"mc-"+ticket.printId()+"\" orderName=\"mc-"+ticket.printId()+"\" orderTypeId=\"SALES_ORDER\" priority=\"2\" productStoreId=\"9000\" remainingSubTotal=\""+String.valueOf(ticket.getTotal())+"\" salesChannelEnumId=\"POS_SALES_CHANNEL\" statusId=\"ORDER_APPROVED\" visitId=\"10342\" webSiteId=\"OrderEntry\"/>");
        fulltext_xml += "<OrderRole createdStamp=\"2013-01-17 10:32:43.188\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.188\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" partyId=\"Company\" roleTypeId=\"BILL_FROM_VENDOR\"/>";
        pf("<OrderRole createdStamp=\"2013-01-17 10:32:43.188\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.188\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" partyId=\"Company\" roleTypeId=\"BILL_FROM_VENDOR\"/>");
        fulltext_xml += "<OrderRole createdStamp=\"2013-01-17 10:32:43.254\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.254\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" partyId=\"DemoCustomer\" roleTypeId=\"PLACING_CUSTOMER\"/>";
        pf("<OrderRole createdStamp=\"2013-01-17 10:32:43.254\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.254\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" partyId=\"DemoCustomer\" roleTypeId=\"PLACING_CUSTOMER\"/>");
        fulltext_xml += "<OrderRole createdStamp=\"2013-01-17 10:32:43.258\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.258\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" partyId=\"DemoCustomer\" roleTypeId=\"BILL_TO_CUSTOMER\"/>";
        pf("<OrderRole createdStamp=\"2013-01-17 10:32:43.261\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.261\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" partyId=\"DemoCustomer\" roleTypeId=\"SHIP_TO_CUSTOMER\"/>");
        fulltext_xml += "<OrderRole createdStamp=\"2013-01-17 10:32:43.261\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.261\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" partyId=\"DemoCustomer\" roleTypeId=\"SHIP_TO_CUSTOMER\"/>";
        pf("<OrderRole createdStamp=\"2013-01-17 10:32:43.261\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.261\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" partyId=\"DemoCustomer\" roleTypeId=\"SHIP_TO_CUSTOMER\"/>");
        pf("<OrderContactMech contactMechId=\"9015\" contactMechPurposeTypeId=\"SHIPPING_LOCATION\" createdStamp=\"2013-01-17 10:32:43.508\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.508\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\"/>");
        fulltext_xml += "<OrderContactMech contactMechId=\"9015\" contactMechPurposeTypeId=\"SHIPPING_LOCATION\" createdStamp=\"2013-01-17 10:32:43.508\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.508\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\"/>";
        pf(  "<OrderContactMech contactMechId=\"9026\" contactMechPurposeTypeId=\"ORDER_EMAIL\" createdStamp=\"2013-01-17 10:32:43.508\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.508\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\"/>");
        fulltext_xml += "<OrderContactMech contactMechId=\"9026\" contactMechPurposeTypeId=\"ORDER_EMAIL\" createdStamp=\"2013-01-17 10:32:43.508\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.508\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\"/>";

        
        
        
       // System.out.println(fulltext_xml);
        // ticket id = MC ID + ticket id
       // fulltext_xml += getSoapSync().createOrderHeader("mc"+ticket.getTicketId(),String.valueOf(ticket.getTotal()),String.valueOf(ticket.getSubTotal()),"POS_SALES_CHANNEL");
       // System.out.println(getSoapSync().createOrderHeader("mc"+ticket.getTicketId(),String.valueOf(ticket.getTotal()),String.valueOf(ticket.getSubTotal()),"POS_SALES_CHANNEL"));
        int status_id  = 5;  
        for (TicketLineInfo l : ticket.getLines()) {
            status_id++;
            fulltext_xml +="<OrderItem createdStamp=\"2013-01-17 10:32:42.729\" createdTxStamp=\"2013-01-17 10:32:32.764\" estimatedDeliveryDate=\"2009-08-13 17:43:53.0\" isModifiedPrice=\"N\" isPromo=\"N\" itemDescription=\"GZ-2644-0 Round Gizmo\" lastUpdatedStamp=\"2013-01-17 10:32:42.729\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" orderItemTypeId=\"PRODUCT_ORDER_ITEM\" prodCatalogId=\"DemoCatalog\" productId=\"GZ-2644\" quantity=\""+String.valueOf(l.getMultiply())+"\" selectedAmount=\"0.000000\" statusId=\"ITEM_COMPLETED\" unitListPrice=\"0.00\" unitPrice=\""+String.valueOf(l.getPrice())+"\"/>";
            pf("<OrderItem createdStamp=\"2013-01-17 10:32:42.729\" createdTxStamp=\"2013-01-17 10:32:32.764\" estimatedDeliveryDate=\"2009-08-13 17:43:53.0\" isModifiedPrice=\"N\" isPromo=\"N\" itemDescription=\"GZ-2644-0 Round Gizmo\" lastUpdatedStamp=\"2013-01-17 10:32:42.729\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" orderItemTypeId=\"PRODUCT_ORDER_ITEM\" prodCatalogId=\"DemoCatalog\" productId=\"GZ-2644\" quantity=\""+String.valueOf(l.getMultiply())+"\" selectedAmount=\"0.000000\" statusId=\"ITEM_COMPLETED\" unitListPrice=\"0.00\" unitPrice=\""+String.valueOf(l.getPrice())+"\"/>");
            // String ticketid, String DemoCatalog, String ticketline_product, String ticketline_unit, String ticketline_price, String ticketline_line, String amount, String taxes_name, String adjustmentid, String taxes_rate, String taxes_ID,String status_id){
            // fulltext_xml += getSoapSync().createOrderItem("mc"+ticket.getTicketId(),"DemoCatalog",l.getProductID(),String.valueOf(l.getMultiply()),String.valueOf(l.getPrice()),String.valueOf(l.getTicketLine()+1),String.valueOf(ticket.getTotal()),l.getTaxInfo().getName(),"",String.valueOf(l.getTaxRate()),l.getTaxInfo().getId(),"mc"+ticket.getTicketId()+status_id);
        }

            
        fulltext_xml += "<OrderShipment createdStamp=\"2012-11-03 20:51:10.528\" createdTxStamp=\"2012-11-03 20:51:09.876\" lastUpdatedStamp=\"2013-01-18 19:25:38.784\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" quantity=\""+String.valueOf(ticket.getLine(0).getMultiply())+"\" shipGroupSeqId=\"00001\" shipmentId=\"9996\" shipmentItemSeqId=\"00001\"/>";
        pf("<OrderShipment createdStamp=\"2012-11-03 20:51:10.528\" createdTxStamp=\"2012-11-03 20:51:09.876\" lastUpdatedStamp=\"2013-01-18 19:25:38.784\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" quantity=\""+String.valueOf(ticket.getLine(0).getMultiply())+"\" shipGroupSeqId=\"00001\" shipmentId=\"9996\" shipmentItemSeqId=\"00001\"/>");
        fulltext_xml += "<OrderAdjustment amount=\""+String.valueOf(ticket.getTotal())+"\" comments=\"Tax Exempt\" createdByUserLogin=\"admin\" createdDate=\"2012-12-12 16:55:02.742\" createdStamp=\"2013-01-18 19:25:38.79\" createdTxStamp=\"2013-01-18 19:25:38.641\" lastUpdatedStamp=\"2013-01-18 19:25:38.79\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" orderAdjustmentId=\"10540\" orderAdjustmentTypeId=\"SALES_TAX\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" primaryGeoId=\"_NA_\" shipGroupSeqId=\"00001\" sourcePercentage=\"0.000000\" taxAuthGeoId=\"_NA_\" taxAuthorityRateSeqId=\"9000\"/>";
        pf("<OrderAdjustment amount=\""+String.valueOf(ticket.getTotal())+"\" comments=\"Tax Exempt\" createdByUserLogin=\"admin\" createdDate=\"2012-12-12 16:55:02.742\" createdStamp=\"2013-01-18 19:25:38.79\" createdTxStamp=\"2013-01-18 19:25:38.641\" lastUpdatedStamp=\"2013-01-18 19:25:38.79\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" orderAdjustmentId=\"10540\" orderAdjustmentTypeId=\"SALES_TAX\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" primaryGeoId=\"_NA_\" shipGroupSeqId=\"00001\" sourcePercentage=\"0.000000\" taxAuthGeoId=\"_NA_\" taxAuthorityRateSeqId=\"9000\"/>");
        fulltext_xml += "<OrderItemShipGroup carrierPartyId=\"UPS\" carrierRoleTypeId=\"CARRIER\" contactMechId=\"9000\" createdStamp=\"2012-12-12 16:55:03.086\" createdTxStamp=\"2012-12-12 16:55:02.477\" estimatedDeliveryDate=\"2009-08-13 17:43:53.0\" isGift=\"N\" lastUpdatedStamp=\"2013-01-18 19:25:38.824\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" maySplit=\"N\" orderId=\"mc-"+ticket.printId()+"\" shipAfterDate=\"2012-12-12 16:54:10.935\" shipByDate=\"2012-12-13 16:54:14.543\" shipGroupSeqId=\"00001\" shipmentMethodTypeId=\"NEXT_DAY\"/>";
        pf("<OrderItemShipGroup carrierPartyId=\"UPS\" carrierRoleTypeId=\"CARRIER\" contactMechId=\"9000\" createdStamp=\"2012-12-12 16:55:03.086\" createdTxStamp=\"2012-12-12 16:55:02.477\" estimatedDeliveryDate=\"2009-08-13 17:43:53.0\" isGift=\"N\" lastUpdatedStamp=\"2013-01-18 19:25:38.824\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" maySplit=\"N\" orderId=\"mc-"+ticket.printId()+"\" shipAfterDate=\"2012-12-12 16:54:10.935\" shipByDate=\"2012-12-13 16:54:14.543\" shipGroupSeqId=\"00001\" shipmentMethodTypeId=\"NEXT_DAY\"/>");
        
        fulltext_xml += "<OrderItemShipGroupAssoc createdStamp=\"2012-12-12 16:55:03.132\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2013-01-18 19:25:38.78\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" quantity=\""+String.valueOf(ticket.getLine(0).getMultiply())+"\" shipGroupSeqId=\"00001\"/>";
        pf("<OrderItemShipGroupAssoc createdStamp=\"2012-12-12 16:55:03.132\" createdTxStamp=\"2012-12-12 16:55:02.477\" lastUpdatedStamp=\"2013-01-18 19:25:38.78\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" quantity=\""+String.valueOf(ticket.getLine(0).getMultiply())+"\" shipGroupSeqId=\"00001\"/>");
        
        fulltext_xml += "<OrderItemShipGrpInvRes createdDatetime=\"2012-12-12 16:55:04.025\" createdStamp=\"2012-12-12 16:55:04.025\" createdTxStamp=\"2012-12-12 16:55:02.477\" currentPromisedDate=\"2012-12-13 16:54:14.543\" inventoryItemId=\"9000\" lastUpdatedStamp=\"2013-01-18 19:25:38.787\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" priority=\"2\" promisedDatetime=\"2012-12-13 16:54:14.543\" quantity=\""+String.valueOf(ticket.getLine(0).getMultiply())+"\" reserveOrderEnumId=\"INVRO_FIFO_REC\" reservedDatetime=\"2012-12-12 16:55:04.025\" shipGroupSeqId=\"00001\"/>";
        pf("<OrderItemShipGrpInvRes createdDatetime=\"2012-12-12 16:55:04.025\" createdStamp=\"2012-12-12 16:55:04.025\" createdTxStamp=\"2012-12-12 16:55:02.477\" currentPromisedDate=\"2012-12-13 16:54:14.543\" inventoryItemId=\"9000\" lastUpdatedStamp=\"2013-01-18 19:25:38.787\" lastUpdatedTxStamp=\"2013-01-18 19:25:38.641\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" priority=\"2\" promisedDatetime=\"2012-12-13 16:54:14.543\" quantity=\""+String.valueOf(ticket.getLine(0).getMultiply())+"\" reserveOrderEnumId=\"INVRO_FIFO_REC\" reservedDatetime=\"2012-12-12 16:55:04.025\" shipGroupSeqId=\"00001\"/>");
        
        fulltext_xml += "<OrderItemPriceInfo createdStamp=\"2013-01-17 10:32:43.092\" createdTxStamp=\"2013-01-17 10:32:32.764\" description=\"SupplierProduct minimumOrderQuantity:0.000000, lastPrice: "+String.valueOf(ticket.getTotal())+"\" lastUpdatedStamp=\"2013-01-17 10:32:43.092\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderItemPriceInfoId=\"9001\" orderItemSeqId=\"00001\"/>";
        pf("<OrderItemPriceInfo createdStamp=\"2013-01-17 10:32:43.092\" createdTxStamp=\"2013-01-17 10:32:32.764\" description=\"SupplierProduct minimumOrderQuantity:0.000000, lastPrice: "+String.valueOf(ticket.getTotal())+"\" lastUpdatedStamp=\"2013-01-17 10:32:43.092\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderItemPriceInfoId=\"9001\" orderItemSeqId=\"00001\"/>");
        
        fulltext_xml += "<OrderPaymentPreference createdStamp=\"2013-01-17 10:29:13.397\" createdTxStamp=\"2013-01-17 10:28:59.639\" lastUpdatedStamp=\"2013-01-17 10:29:13.397\" lastUpdatedTxStamp=\"2013-01-17 10:28:59.639\" maxAmount=\""+String.valueOf(ticket.getTotal())+"\" orderId=\"mc-"+ticket.printId()+"\" orderPaymentPreferenceId=\"mc-"+ticket.printId()+"\" overflowFlag=\"N\" paymentMethodTypeId=\"EXT_EBAY\" presentFlag=\"N\" statusId=\"PAYMENT_NOT_RECEIVED\" swipedFlag=\"N\"/>";
        pf("<OrderPaymentPreference createdStamp=\"2013-01-17 10:29:13.397\" createdTxStamp=\"2013-01-17 10:28:59.639\" lastUpdatedStamp=\"2013-01-17 10:29:13.397\" lastUpdatedTxStamp=\"2013-01-17 10:28:59.639\" maxAmount=\""+String.valueOf(ticket.getTotal())+"\" orderId=\"mc-"+ticket.printId()+"\" orderPaymentPreferenceId=\"mc-"+ticket.printId()+"\" overflowFlag=\"N\" paymentMethodTypeId=\"EXT_EBAY\" presentFlag=\"N\" statusId=\"PAYMENT_NOT_RECEIVED\" swipedFlag=\"N\"/>");
        
        fulltext_xml += "<OrderStatus createdStamp=\"2013-01-17 10:32:43.319\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.319\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderStatusId=\"9009\" statusDatetime=\"2009-08-13 17:45:50.419\" statusId=\"ORDER_CREATED\" statusUserLogin=\"admin\"/>";
        pf("<OrderStatus createdStamp=\"2013-01-17 10:32:43.319\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.319\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderStatusId=\"9009\" statusDatetime=\"2009-08-13 17:45:50.419\" statusId=\"ORDER_CREATED\" statusUserLogin=\"admin\"/>");
        fulltext_xml += "<OrderStatus createdStamp=\"2013-01-17 10:32:43.428\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.428\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" orderStatusId=\"9010\" statusDatetime=\"2009-08-13 17:45:50.419\" statusId=\"ITEM_CREATED\" statusUserLogin=\"admin\"/>";
        pf("<OrderStatus createdStamp=\"2013-01-17 10:32:43.428\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.428\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" orderStatusId=\"9010\" statusDatetime=\"2009-08-13 17:45:50.419\" statusId=\"ITEM_CREATED\" statusUserLogin=\"admin\"/>");
        fulltext_xml += "<OrderStatus createdStamp=\"2013-01-17 10:32:43.488\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.488\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" orderStatusId=\"9011\" statusDatetime=\"2009-08-13 17:46:15.144\" statusId=\"ITEM_APPROVED\" statusUserLogin=\"admin\"/>";
        pf("<OrderStatus createdStamp=\"2013-01-17 10:32:43.488\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.488\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" orderStatusId=\"9011\" statusDatetime=\"2009-08-13 17:46:15.144\" statusId=\"ITEM_APPROVED\" statusUserLogin=\"admin\"/>");
        fulltext_xml += "<OrderStatus createdStamp=\"2013-01-17 10:32:43.498\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.498\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderStatusId=\"9012\" statusDatetime=\"2009-08-13 17:46:15.206\" statusId=\"ORDER_APPROVED\" statusUserLogin=\"admin\"/>";
        pf("<OrderStatus createdStamp=\"2013-01-17 10:32:43.498\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.498\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderStatusId=\"9012\" statusDatetime=\"2009-08-13 17:46:15.206\" statusId=\"ORDER_APPROVED\" statusUserLogin=\"admin\"/>");
        fulltext_xml += "<OrderStatus createdStamp=\"2013-01-17 10:32:43.499\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.499\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" orderStatusId=\"9013\" statusDatetime=\"2009-08-13 17:47:47.292\" statusId=\"ITEM_COMPLETED\" statusUserLogin=\"admin\"/>";
        pf("<OrderStatus createdStamp=\"2013-01-17 10:32:43.499\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.499\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderItemSeqId=\"00001\" orderStatusId=\"9013\" statusDatetime=\"2009-08-13 17:47:47.292\" statusId=\"ITEM_COMPLETED\" statusUserLogin=\"admin\"/>");
        fulltext_xml += "<OrderStatus createdStamp=\"2013-01-17 10:32:43.501\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.501\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderStatusId=\"9014\" statusDatetime=\"2009-08-13 17:47:47.344\" statusId=\"ORDER_COMPLETED\" statusUserLogin=\"admin\"/>";
        pf("<OrderStatus createdStamp=\"2013-01-17 10:32:43.501\" createdTxStamp=\"2013-01-17 10:32:32.764\" lastUpdatedStamp=\"2013-01-17 10:32:43.501\" lastUpdatedTxStamp=\"2013-01-17 10:32:32.764\" orderId=\"mc-"+ticket.printId()+"\" orderStatusId=\"9014\" statusDatetime=\"2009-08-13 17:47:47.344\" statusId=\"ORDER_COMPLETED\" statusUserLogin=\"admin\"/>");
        
        fulltext_xml += "</entity-engine-xml>";
        pf("</entity-engine-xml>");
        getSoapSync().syncData(fulltext_xml);

        
           try{
              //pf(getSoapSync().createOrderAll("mc"+ticket.getTicketId(),"ticket.getCustomerId()",ticket.getUser().getId(),String.valueOf(ticket.getTotal()),"mc"+ticket.getTicketId(),String.valueOf(ticket.getTotal()),ticket.getUser().getName(),String.valueOf(ticket.getTax()),String.valueOf(ticket.getTax()),ticket.getTransactionID(),ticket.getLine(0).getTaxInfo().getTaxCategoryID(),ticket.getCustomer().getSearchkey(),ticket.getCustomer().getTaxid(),ticket.getCustomer().getName(),"",ticket.getLine(0).getTaxInfo().getValidFrom().toString(),ticket.getLine(0).getTaxInfo().getTaxCategoryID()));
              if(ticket.getCustomer()==null) pf("getCustomer = null");
              if(ticket.getCustomerId()==null) pf("getCustomerId = null");

              pf(" getId "+ticket.getTicketId());
              pf(" ticket.getCustomerId()"+ticket.getCustomerId());
              pf(" printId "+ticket.printId());
              pf(" getName "+ticket.getName());
              pf(" Count line "+ticket.getLinesCount());
              pf(" getUsername "+ticket.getUser().getName());
              pf(" getTicketId "+ticket.getTicketId());           
              pf(" getTransactionID "+ticket.getTransactionID());
              pf(" getTaxCategoryID "+ticket.getLine(0).getTaxInfo().getTaxCategoryID());
              
             if(ticket.getCustomer()==null)
                  fulltext_xml += getSoapSync().createOrderAll("mc"+ticket.getTicketId(),"ticket.getCustomerId()",ticket.getUser().getId(),String.valueOf(ticket.getTotal()),"mc"+ticket.getTicketId(),String.valueOf(ticket.getTotal()),ticket.getUser().getName(),String.valueOf(ticket.getTax()),String.valueOf(ticket.getTax()),ticket.getTransactionID(),ticket.getLine(0).getTaxInfo().getTaxCategoryID(),"ticket.getCustomer().getSearchkey()","ticket.getCustomer().getTaxid()","ticket.getCustomer().getName()","",ticket.getLine(0).getTaxInfo().getValidFrom().toString(),ticket.getLine(0).getTaxInfo().getTaxCategoryID());
             else fulltext_xml += getSoapSync().createOrderAll("mc"+ticket.getTicketId(),"ticket.getCustomerId()",ticket.getUser().getId(),String.valueOf(ticket.getTotal()),"mc"+ticket.getTicketId(),String.valueOf(ticket.getTotal()),ticket.getUser().getName(),String.valueOf(ticket.getTax()),String.valueOf(ticket.getTax()),ticket.getTransactionID(),ticket.getLine(0).getTaxInfo().getTaxCategoryID(),ticket.getCustomer().getSearchkey(),ticket.getCustomer().getTaxid(),ticket.getCustomer().getName(),"",ticket.getLine(0).getTaxInfo().getValidFrom().toString(),ticket.getLine(0).getTaxInfo().getTaxCategoryID());
             // pf(fulltext_xml += getSoapSync().createOrderAll("mc"+ticket.getTicketId(),"ticket.getCustomerId()",ticket.getUser().getId(),String.valueOf(ticket.getTotal()),"mc"+ticket.getTicketId(),String.valueOf(ticket.getTotal()),ticket.getUser().getName(),String.valueOf(ticket.getTax()),String.valueOf(ticket.getTax()),ticket.getTransactionID(),ticket.getLine(0).getTaxInfo().getTaxCategoryID(),ticket.getCustomer().getSearchkey(),ticket.getCustomer().getTaxid(),ticket.getCustomer().getName(),"",ticket.getLine(0).getTaxInfo().getValidFrom().toString(),ticket.getLine(0).getTaxInfo().getTaxCategoryID()));
           }catch(Exception e){
               System.out.println("++++Error "+e.getMessage());
           }
       // fulltext_xml += getSoapSync().createOrderItem("", fulltext_xml, fulltext_xml, location, location, location, fulltext_xml, location)
       // fulltext_xml += getSoapSync().createOrderRelations("","");
            //   fulltext_xml += "</entity-engine-xml>";
            //   getSoapSync().syncData(fulltext_xml);
        } catch (AxisFault ex) {
            //   Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void pf(String txt){
        System.out.println(""+txt+"");
    }
    SAOPSync saops=null;
    public SAOPSync getSoapSync(){
        if(saops==null) saops = new SAOPSync();
        return saops;
    }
    public final void deleteTicket(final TicketInfo ticket, final String location) throws BasicException {

        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {

                // update the inventory
                Date d = new Date();
                for (int i = 0; i < ticket.getLinesCount(); i++) {
                    if (ticket.getLine(i).getProductID() != null)  {
                        // Hay que actualizar el stock si el hay producto
                        getStockDiaryInsert().exec( new Object[] {
                            UUID.randomUUID().toString(),
                            d,
                            ticket.getLine(i).getMultiply() >= 0.0
                                ? MovementReason.IN_REFUND.getKey()
                                : MovementReason.OUT_SALE.getKey(),
                            location,
                            ticket.getLine(i).getProductID(),
                            ticket.getLine(i).getProductAttSetInstId(),
                            new Double(ticket.getLine(i).getMultiply()),
                            new Double(ticket.getLine(i).getPrice())
                        });
                    }
                }

                // update customer debts
                for (PaymentInfo p : ticket.getPayments()) {
                    if ("debt".equals(p.getName()) || "debtpaid".equals(p.getName())) {

                        // udate customer fields...
                        ticket.getCustomer().updateCurDebt(-p.getTotal(), ticket.getDate());

                         // save customer fields...
                        getDebtUpdate().exec(new DataParams() { public void writeValues() throws BasicException {
                            setDouble(1, ticket.getCustomer().getCurdebt());
                            setTimestamp(2, ticket.getCustomer().getCurdate());
                            setString(3, ticket.getCustomer().getId());
                        }});
                    }
                }

                // and delete the receipt
                new StaticSentence(s
                    , "DELETE FROM TAXLINES WHERE RECEIPT = ?"
                    , SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s
                    , "DELETE FROM PAYMENTS WHERE RECEIPT = ?"
                    , SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s
                    , "DELETE FROM TICKETLINES WHERE TICKET = ?"
                    , SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s
                    , "DELETE FROM TICKETS WHERE ID = ?"
                    , SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s
                    , "DELETE FROM RECEIPTS WHERE ID = ?"
                    , SerializerWriteString.INSTANCE).exec(ticket.getId());
                return null;
            }
        };
        t.execute();
    }

    public final Integer getNextTicketIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM").find();
    }

    public final Integer getNextTicketRefundIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM_REFUND").find();
    }

    public final Integer getNextTicketPaymentIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM_PAYMENT").find();
    }

    public final SentenceList getProductCatQBF() {
        return new StaticSentence(s
            , new QBFBuilder(
                "SELECT P.ID, P.REFERENCE, P.CODE, P.NAME, P.ISCOM, P.ISSCALE, P.PRICEBUY, P.PRICESELL, P.CATEGORY, P.TAXCAT, P.ATTRIBUTESET_ID, P.IMAGE, P.STOCKCOST, P.STOCKVOLUME, CASE WHEN C.PRODUCT IS NULL THEN " + s.DB.FALSE() + " ELSE " + s.DB.TRUE() + " END, C.CATORDER, P.ATTRIBUTES " +
                "FROM PRODUCTS P LEFT OUTER JOIN PRODUCTS_CAT C ON P.ID = C.PRODUCT " +
                "WHERE ?(QBF_FILTER) " +
                "ORDER BY P.REFERENCE", new String[] {"P.NAME", "P.PRICEBUY", "P.PRICESELL", "P.CATEGORY", "P.CODE"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING})
            , productsRow.getSerializerRead());
    }

    public final SentenceExec getProductCatInsert() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                int i = new PreparedSentence(s
                    , "INSERT INTO PRODUCTS (ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, CATEGORY, TAXCAT, ATTRIBUTESET_ID, IMAGE, STOCKCOST, STOCKVOLUME, ATTRIBUTES) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    , new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 16})).exec(params);
                if (i > 0 && ((Boolean)values[14]).booleanValue()) {
                    return new PreparedSentence(s
                        , "INSERT INTO PRODUCTS_CAT (PRODUCT, CATORDER) VALUES (?, ?)"
                        , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {0, 15})).exec(params);
                } else {
                    return i;
                }
            }
        };
    }

    public final SentenceExec getProductCatUpdate() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                int i = new PreparedSentence(s
                    , "UPDATE PRODUCTS SET ID = ?, REFERENCE = ?, CODE = ?, NAME = ?, ISCOM = ?, ISSCALE = ?, PRICEBUY = ?, PRICESELL = ?, CATEGORY = ?, TAXCAT = ?, ATTRIBUTESET_ID = ?, IMAGE = ?, STOCKCOST = ?, STOCKVOLUME = ?, ATTRIBUTES = ? WHERE ID = ?"
                    , new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 16, 0})).exec(params);
                if (i > 0) {
                    if (((Boolean)values[14]).booleanValue()) {
                        if (new PreparedSentence(s
                                , "UPDATE PRODUCTS_CAT SET CATORDER = ? WHERE PRODUCT = ?"
                                , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {15, 0})).exec(params) == 0) {
                            new PreparedSentence(s
                                , "INSERT INTO PRODUCTS_CAT (PRODUCT, CATORDER) VALUES (?, ?)"
                                , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {0, 15})).exec(params);
                        }
                    } else {
                        new PreparedSentence(s
                            , "DELETE FROM PRODUCTS_CAT WHERE PRODUCT = ?"
                            , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {0})).exec(params);
                    }
                }
                return i;
            }
        };
    }

    public final SentenceExec getProductCatDelete() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s
                    , "DELETE FROM PRODUCTS_CAT WHERE PRODUCT = ?"
                    , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {0})).exec(params);
                return new PreparedSentence(s
                    , "DELETE FROM PRODUCTS WHERE ID = ?"
                    , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {0})).exec(params);
            }
        };
    }

    public final SentenceExec getDebtUpdate() {

        return new PreparedSentence(s
                , "UPDATE CUSTOMERS SET CURDEBT = ?, CURDATE = ? WHERE ID = ?"
                , SerializerWriteParams.INSTANCE);
    }

    public final SentenceExec getStockDiaryInsert() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                int updateresult = ((Object[]) params)[5] == null // si ATTRIBUTESETINSTANCE_ID is null
                    ? new PreparedSentence(s
                        , "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL"
                        , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {6, 3, 4})).exec(params)
                    : new PreparedSentence(s
                        , "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?"
                        , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {6, 3, 4, 5})).exec(params);

                if (updateresult == 0) {
                    new PreparedSentence(s
                        , "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS) VALUES (?, ?, ?, ?)"
                        , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {3, 4, 5, 6})).exec(params);
                }
                return new PreparedSentence(s
                    , "INSERT INTO STOCKDIARY (ID, DATENEW, REASON, LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {0, 1, 2, 3, 4, 5, 6, 7})).exec(params);
            }
        };
    }

    public final SentenceExec getStockDiaryDelete() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                int updateresult = ((Object[]) params)[5] == null // if ATTRIBUTESETINSTANCE_ID is null
                        ? new PreparedSentence(s
                            , "UPDATE STOCKCURRENT SET UNITS = (UNITS - ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL"
                            , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {6, 3, 4})).exec(params)
                        : new PreparedSentence(s
                            , "UPDATE STOCKCURRENT SET UNITS = (UNITS - ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?"
                            , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {6, 3, 4, 5})).exec(params);

                if (updateresult == 0) {
                    new PreparedSentence(s
                        , "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS) VALUES (?, ?, ?, -(?))"
                        , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {3, 4, 5, 6})).exec(params);
                }
                return new PreparedSentence(s
                    , "DELETE FROM STOCKDIARY WHERE ID = ?"
                    , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {0})).exec(params);
            }
        };
    }

    public final SentenceExec getPaymentMovementInsert() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s
                    , "INSERT INTO RECEIPTS (ID, MONEY, DATENEW) VALUES (?, ?, ?)"
                    , new SerializerWriteBasicExt(paymenttabledatas, new int[] {0, 1, 2})).exec(params);
                return new PreparedSentence(s
                    , "INSERT INTO PAYMENTS (ID, RECEIPT, PAYMENT, TOTAL) VALUES (?, ?, ?, ?)"
                    , new SerializerWriteBasicExt(paymenttabledatas, new int[] {3, 0, 4, 5})).exec(params);
            }
        };
    }

    public final SentenceExec getPaymentMovementDelete() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s
                    , "DELETE FROM PAYMENTS WHERE ID = ?"
                    , new SerializerWriteBasicExt(paymenttabledatas, new int[] {3})).exec(params);
                return new PreparedSentence(s
                    , "DELETE FROM RECEIPTS WHERE ID = ?"
                    , new SerializerWriteBasicExt(paymenttabledatas, new int[] {0})).exec(params);
            }
        };
    }

    public final double findProductStock(String warehouse, String id, String attsetinstid) throws BasicException {

        PreparedSentence p = attsetinstid == null
                ? new PreparedSentence(s, "SELECT UNITS FROM STOCKCURRENT WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL"
                    , new SerializerWriteBasic(Datas.STRING, Datas.STRING)
                    , SerializerReadDouble.INSTANCE)
                : new PreparedSentence(s, "SELECT UNITS FROM STOCKCURRENT WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?"
                    , new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING)
                    , SerializerReadDouble.INSTANCE);

        Double d = (Double) p.find(warehouse, id, attsetinstid);
        return d == null ? 0.0 : d.doubleValue();
    }

    public final SentenceExec getCatalogCategoryAdd() {
        return new StaticSentence(s
                , "INSERT INTO PRODUCTS_CAT(PRODUCT, CATORDER) SELECT ID, " + s.DB.INTEGER_NULL() + " FROM PRODUCTS WHERE CATEGORY = ?"
                , SerializerWriteString.INSTANCE);
    }
    public final SentenceExec getCatalogCategoryDel() {
        return new StaticSentence(s
                , "DELETE FROM PRODUCTS_CAT WHERE PRODUCT = ANY (SELECT ID FROM PRODUCTS WHERE CATEGORY = ?)"
                , SerializerWriteString.INSTANCE);
    }

    public final TableDefinition getTableCategories() {
        return new TableDefinition(s,
            "CATEGORIES"
            , new String[] {"ID", "NAME", "PARENTID", "IMAGE"}
            , new String[] {"ID", AppLocal.getIntString("Label.Name"), "", AppLocal.getIntString("label.image")}
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.IMAGE}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.STRING, Formats.NULL}
            , new int[] {0}
        );
    }
    public final TableDefinition getTableTaxes() {
        return new TableDefinition(s,
            "TAXES"
            , new String[] {"ID", "NAME", "CATEGORY", "VALIDFROM", "CUSTCATEGORY", "PARENTID", "RATE", "RATECASCADE", "RATEORDER"}
            , new String[] {"ID", AppLocal.getIntString("Label.Name"), AppLocal.getIntString("label.taxcategory"), AppLocal.getIntString("Label.ValidFrom"), AppLocal.getIntString("label.custtaxcategory"), AppLocal.getIntString("label.taxparent"), AppLocal.getIntString("label.dutyrate"), AppLocal.getIntString("label.cascade"), AppLocal.getIntString("label.order")}
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.TIMESTAMP, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.BOOLEAN, Datas.INT}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.STRING, Formats.TIMESTAMP, Formats.STRING, Formats.STRING, Formats.PERCENT, Formats.BOOLEAN, Formats.INT}
            , new int[] {0}
        );
    }

    public final TableDefinition getTableTaxCustCategories() {
        return new TableDefinition(s,
            "TAXCUSTCATEGORIES"
            , new String[] {"ID", "NAME"}
            , new String[] {"ID", AppLocal.getIntString("Label.Name")}
            , new Datas[] {Datas.STRING, Datas.STRING}
            , new Formats[] {Formats.STRING, Formats.STRING}
            , new int[] {0}
        );
    }
    public final TableDefinition getTableTaxCategories() {
        return new TableDefinition(s,
            "TAXCATEGORIES"
            , new String[] {"ID", "NAME"}
            , new String[] {"ID", AppLocal.getIntString("Label.Name")}
            , new Datas[] {Datas.STRING, Datas.STRING}
            , new Formats[] {Formats.STRING, Formats.STRING}
            , new int[] {0}
        );
    }

    public final TableDefinition getTableLocations() {
        return new TableDefinition(s,
            "LOCATIONS"
            , new String[] {"ID", "NAME", "ADDRESS"}
            , new String[] {"ID", AppLocal.getIntString("label.locationname"), AppLocal.getIntString("label.locationaddress")}
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.STRING}
            , new int[] {0}
        );
    }

    protected static class CustomerExtRead implements SerializerRead {
        public Object readValues(DataRead dr) throws BasicException {
            CustomerInfoExt c = new CustomerInfoExt(dr.getString(1));
            c.setTaxid(dr.getString(2));
            c.setSearchkey(dr.getString(3));
            c.setName(dr.getString(4));
            c.setCard(dr.getString(5));
            c.setTaxCustomerID(dr.getString(6));
            c.setNotes(dr.getString(7));
            c.setMaxdebt(dr.getDouble(8));
            c.setVisible(dr.getBoolean(9).booleanValue());
            c.setCurdate(dr.getTimestamp(10));
            c.setCurdebt(dr.getDouble(11));
            c.setFirstname(dr.getString(12));
            c.setLastname(dr.getString(13));
            c.setEmail(dr.getString(14));
            c.setPhone(dr.getString(15));
            c.setPhone2(dr.getString(16));
            c.setFax(dr.getString(17));
            c.setAddress(dr.getString(18));
            c.setAddress2(dr.getString(19));
            c.setPostal(dr.getString(20));
            c.setCity(dr.getString(21));
            c.setRegion(dr.getString(22));
            c.setCountry(dr.getString(23));

            return c;
        }
    }
}
