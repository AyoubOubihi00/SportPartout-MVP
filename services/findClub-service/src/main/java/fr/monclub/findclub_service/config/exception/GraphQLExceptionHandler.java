package fr.monclub.findclub_service.config.exception;

import fr.monclub.findclub_service.core.exception.ClubAccessDeniedException;
import fr.monclub.findclub_service.core.exception.ClubNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof ClubNotFoundException) {
            return GraphqlErrorBuilder.newError()
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .build();
        }

        if (ex instanceof ClubAccessDeniedException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.UNAUTHORIZED)
                    .build();
        }

        return GraphqlErrorBuilder.newError()
                .message("Internal server error")
                .path(env.getExecutionStepInfo().getPath())
                .build();
    }
}
