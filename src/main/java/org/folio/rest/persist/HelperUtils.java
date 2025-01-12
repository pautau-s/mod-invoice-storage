package org.folio.rest.persist;

import static org.folio.rest.persist.PgUtil.response;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class HelperUtils {
  private static final Logger log = LoggerFactory.getLogger(HelperUtils.class);
  
  private static final String IL_NUMBER_PREFIX = "ilNumber_";
  private static final String QUOTES_SYMBOL = "\"";

  private HelperUtils() {
    throw new UnsupportedOperationException("Cannot instantiate utility class.");
  }

  public static <T, E> void getEntitiesCollection(EntitiesMetadataHolder<T, E> entitiesMetadataHolder, QueryHolder queryHolder, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext, Map<String, String> okapiHeaders) {
		String[] fieldList = { "*" };

		final Method respond500;

		try {
			respond500 = entitiesMetadataHolder.getRespond500WithTextPlainMethod();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			asyncResultHandler.handle(response(e.getMessage(), null, null));
			return;
		}

    try {
      Method respond200 = entitiesMetadataHolder.getRespond200WithApplicationJson();
      Method respond400 = entitiesMetadataHolder.getRespond400WithTextPlainMethod();
      PostgresClient postgresClient = PgUtil.postgresClient(vertxContext, okapiHeaders);
      postgresClient.get(queryHolder.getTable(), entitiesMetadataHolder.getClazz(), fieldList, queryHolder.buildCQLQuery(), true, false, reply -> {
        try {
          if (reply.succeeded()) {
            E collection = entitiesMetadataHolder.getCollectionClazz().newInstance();
            List<T> results = reply.result().getResults();
            Method setResults =  entitiesMetadataHolder.getSetResultsMethod();
            Method setTotalRecordsMethod =  entitiesMetadataHolder.getSetTotalRecordsMethod();
            setResults.invoke(collection, results);
            Integer totalRecords = reply.result().getResultInfo().getTotalRecords();
            setTotalRecordsMethod.invoke(collection, totalRecords);
            asyncResultHandler.handle(response(collection, respond200, respond500));
          } else {
            asyncResultHandler.handle(response(reply.cause().getLocalizedMessage(), respond400, respond500));
          }
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          asyncResultHandler.handle(response(e.getMessage(), respond500, respond500));
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      asyncResultHandler.handle(response(e.getMessage(), respond500, respond500));
    }
  }
  
  public enum SequenceQuery {

    CREATE_SEQUENCE {
      @Override
      public String getQuery(String invoiceId) {
        return "CREATE SEQUENCE IF NOT EXISTS " + constructSequenceName(invoiceId) + " MINVALUE 1 MAXVALUE 999";
      }
    },
    GET_IL_NUMBER_FROM_SEQUENCE {
      @Override
      public String getQuery(String invoiceId) {
        return "SELECT * FROM NEXTVAL('" + constructSequenceName(invoiceId) + "')";
      }
    },
    DROP_SEQUENCE {
      @Override
      public String getQuery(String invoiceId) {
        return "DROP SEQUENCE IF EXISTS " + constructSequenceName(invoiceId);
      }
    };

    private static String constructSequenceName(String invoiceId) {
      return QUOTES_SYMBOL + IL_NUMBER_PREFIX + invoiceId + QUOTES_SYMBOL;
    }

    public abstract String getQuery(String invoiceId);
  }
}
