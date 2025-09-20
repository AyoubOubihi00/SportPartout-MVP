package fr.monclub.club_service.config.exception;

import fr.monclub.club_service.core.exception.ClubAccessDeniedException;
import fr.monclub.club_service.core.exception.ClubNotFoundException;
import fr.monclub.club_service.core.exception.ValidationException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import graphql.schema.DataFetchingEnvironment;

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

        if (ex instanceof ValidationException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        }

        return GraphqlErrorBuilder.newError()
                .message("Internal server error")
                .path(env.getExecutionStepInfo().getPath())
                .build();
    }
}
