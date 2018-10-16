package br.ufal.ic.academico;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Willy
 */
@Getter
@Setter
@ToString
public class ConfigApp extends Configuration {
    
    private String university;
    private String state;
    private int port;
    
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();
}