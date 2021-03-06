package io.whalebone.publicapi.ejb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.whalebone.publicapi.ejb.dto.EReason;
import io.whalebone.publicapi.ejb.dto.EThreatType;
import io.whalebone.publicapi.ejb.dto.EventDTO;
import io.whalebone.publicapi.ejb.dto.GeoIpDTO;
import io.whalebone.publicapi.ejb.dto.aggregate.EDnsAggregate;
import io.whalebone.publicapi.ejb.dto.aggregate.EDnsSecAggregate;
import io.whalebone.publicapi.ejb.elastic.Elastic;
import io.whalebone.publicapi.ejb.elastic.ElasticClientProvider;
import io.whalebone.publicapi.ejb.json.ArchiveMappedDeserializer;
import io.whalebone.publicapi.ejb.json.LowercaseEnumTypeAdapter;
import io.whalebone.publicapi.ejb.json.ZonedDateTimeAdapter;
import org.elasticsearch.forarchive.client.Client;
import org.elasticsearch.client.RestHighLevelClient;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.logging.Logger;

@Dependent
public class Resources implements Serializable {
    private static final long serialVersionUID = -5254553036199459988L;

    @Inject
    private ElasticClientProvider elasticClientProvider;

    @Produces
    @Default
    public Logger getLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    @Produces
    @Default
    public RestHighLevelClient getElasticClient() {
        return elasticClientProvider.getClient();
    }

    @Produces
    @Default
    public Client getIoCElasticClient() {
        return elasticClientProvider.getIocElasticClient();
    }

    @Produces
    @Elastic
    public Gson getGson() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(EventDTO.class, new ArchiveMappedDeserializer())
                .registerTypeAdapter(GeoIpDTO.class, new ArchiveMappedDeserializer())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
                .registerTypeAdapter(EDnsAggregate.class, new LowercaseEnumTypeAdapter<>(EDnsAggregate.class))
                .registerTypeAdapter(EDnsSecAggregate.class, new LowercaseEnumTypeAdapter<>(EDnsSecAggregate.class))
                .registerTypeAdapter(EReason.class, new LowercaseEnumTypeAdapter<>(EReason.class))
                .registerTypeAdapter(EThreatType.class, new LowercaseEnumTypeAdapter<>(EThreatType.class))
                // DON'T register adapter for EDnsQueryType since archive has this enum vals defined with uppercase
                .create();
    }
}
