package io.versehub.infrastructure.flip;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import io.versehub.domain.flip.FlipRepository;
import io.versehub.domain.flip.model.Flip;
import io.versehub.domain.flip.model.FlipRecent;
import io.versehub.infrastructure.common.repo.PgAbstractRepo;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletionStage;

@Slf4j
@Singleton
public class PgFlipRepository extends PgAbstractRepo implements FlipRepository {

    @Inject
    protected PgFlipRepository(@NonNull PgConnectionSupplier pgConnectionSupplier) {
        super(pgConnectionSupplier);
    }

    private Flip toFlip(Row row) {
        if (row == null) {
            return null;
        }
        return Flip.builder()//
                .flipId(row.getInteger("flip_id"))
                .wallet(row.getString("wallet"))
                .userName(row.getString("user_name"))
                .avatarId(row.getInteger("avatar_id"))
                .amount(row.getBigDecimal("amount"))
                .flipChoice(row.getInteger("toss_point"))
                .jackPortReward(row.getInteger("jackpot_reward"))
                .tokenId(row.getInteger("token_id"))
                .typeId(row.getInteger("type_id"))
                .flipResult(row.getInteger("flip_result"))
                .time(row.getLong("block_timestamp"))
                .build();
    }

    private FlipRecent toFlipRecent(Row row) {
        if (row == null) {
            return null;
        }
        return FlipRecent.builder()//
                .flipId(row.getInteger("flip_id"))
                .wallet(row.getString("wallet"))
                .userName(row.getString("user_name"))
                .avatarId(row.getInteger("avatar_id"))
                .amount(row.getBigDecimal("amount"))
                .flipChoice(row.getInteger("toss_point"))
                .jackPortReward(row.getInteger("jackpot_reward"))
                .tokenId(row.getInteger("token_id"))
                .typeId(row.getInteger("type_id"))
                .flipResult(row.getInteger("flip_result"))
                .time(row.getLong("block_timestamp"))
                .build();
    }

    @Override
    public CompletionStage<Flip> getById(Integer flipId) {
        return executeThenGetFirst(Table.FLIP.getById(flipId), this::toFlip)
                .exceptionally(e -> {
                    log.error("Error while get flip by Id", e);
                    return ExceptionUtils.rethrow(e);
                });
    }

    private Collection<FlipRecent> toListFlipRecent(RowSet<Row> rowSet) {
        if (rowSet == null || rowSet.size() == 0) {
            return Collections.emptyList();
        }

        var result = new ArrayList<FlipRecent>();

        for (Row row : rowSet) {
            result.add(toFlipRecent(row));
        }
        return result;
    }

    @Override
    public CompletionStage<Collection<FlipRecent>> getFlipRecent() {
        return executeThenGet(Table.FLIP.getFlipRecent())
                .thenApply(this::toListFlipRecent)
                .exceptionally(e -> {
                    log.error("Error while get flip by Id", e);
                    return ExceptionUtils.rethrow(e);
                });
    }
}
