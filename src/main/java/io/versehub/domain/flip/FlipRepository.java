package io.versehub.domain.flip;

import io.versehub.domain.flip.model.Flip;
import io.versehub.domain.flip.model.FlipRecent;

import java.util.Collection;
import java.util.concurrent.CompletionStage;

public interface FlipRepository {

    CompletionStage<Flip> getById (Integer flipId);

    CompletionStage<Collection<FlipRecent>> getFlipRecent ();

}

