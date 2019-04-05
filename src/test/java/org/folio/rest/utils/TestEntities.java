package org.folio.rest.utils;


import org.folio.rest.jaxrs.model.Invoice;
import org.folio.rest.jaxrs.model.InvoiceLine;

public enum TestEntities {
  INVOICE("/invoice-storage/invoices", Invoice.class, "invoice.sample", "note", "Updated note for invoice", 0),
  INVOICE_LINES("/invoice-storage/invoice-lines", InvoiceLine.class, "invoice_line.sample", "quantity", 5, 0);

  TestEntities(String endpoint, Class<?> clazz, String sampleFileName, String updatedFieldName, Object updatedFieldValue, int initialQuantity) {
    this.endpoint = endpoint;
    this.clazz = clazz;
    this.sampleFileName = sampleFileName;
    this.updatedFieldName = updatedFieldName;
    this.updatedFieldValue = updatedFieldValue;
    this.initialQuantity = initialQuantity;
  }

  private int initialQuantity;
  private String endpoint;
  private String sampleFileName;
  private String updatedFieldName;
  private Object updatedFieldValue;
  private Class<?> clazz;

  public String getEndpoint() {
    return endpoint;
  }

  public String getEndpointWithId() {
    return endpoint + "/{id}";
  }

  public String getSampleFileName() {
    return sampleFileName;
  }

  public String getUpdatedFieldName() {
    return updatedFieldName;
  }

  public Object getUpdatedFieldValue() {
    return updatedFieldValue;
  }

  public int getInitialQuantity() {
    return initialQuantity;
  }
  
  public Class<?> getClazz() {
    return clazz;
  }
}