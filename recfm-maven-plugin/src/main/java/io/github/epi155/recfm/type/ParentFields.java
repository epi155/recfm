package io.github.epi155.recfm.type;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public interface ParentFields /*extends IndentAble*/ {
    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ParentFields.class);

    String getName();

    List<NakedField> getFields();

    int getLength();

    default int evalPadWidth(int min) {
        NuclearInt wid = new NuclearInt(min);
        getFields().forEach(it -> {
            if (it instanceof FloatingField) {
                FloatingField fld = (FloatingField) it;
                wid.maxOf(fld.getName().length());
            } else if (it instanceof ParentFields) {
                wid.maxOf(((ParentFields) it).evalPadWidth(wid.get()));
            }
        });
        return wid.get();
    }

    default boolean noHole(int bias) {
        log.info("  * Checking for hole in group {}: [{}..{}] ...", getName(), bias, bias + getLength() - 1);
        boolean[] b = new boolean[getLength()];
        getFields().forEach(it -> it.mark(b, bias));
        List<Integer> hole = new ArrayList<>();
        for (int k = 0; k < getLength(); k++) {
            if (!b[k]) hole.add(k);
        }
        if (hole.isEmpty()) {
            return noHoleChilds();
        } else {
            DisplayHoles dis = new DisplayHoles();
            hole.forEach(dis::add);
            dis.close();
            return false;
        }
    }

    default boolean noHoleChilds() {
        AtomicInteger nmFail = new AtomicInteger(0);
        getFields().forEach(it -> {
            if (it instanceof ParentFields) {
                ParentFields par = (ParentFields) it;
                if (!par.noHole()) nmFail.incrementAndGet();
            }
        });
        if (nmFail.get() == 0) {
            log.info("  * No hole detected in {}.", getName());
            return true;
        } else {
            log.info("  * Hole detected in sub-group.");
            return false;
        }
    }

    default boolean noOverlap(int bias) {
        log.info("  * Checking for overlap in group {}: [{}..{}] ...", getName(), bias, bias + getLength() - 1);
        @SuppressWarnings("unchecked") Set<String>[] b = new Set[getLength()];
        getFields().forEach(it -> it.mark(b, bias));
        List<Pair<Integer, Set<String>>> over = new ArrayList<>();
        for (int k = 0; k < getLength(); k++) {
            if (b[k] != null && b[k].size() > 1) over.add(new ImmutablePair<>(k + bias, b[k]));
        }
        if (over.isEmpty()) {
            return noOverlapChilds();
        } else {
            DisplayOver dis = new DisplayOver();
            over.forEach(dis::add);
            dis.close();
            return false;
        }
    }

    default boolean noOverlapChilds() {
        AtomicInteger nmFail = new AtomicInteger(0);
        getFields().forEach(it -> {
            if (it instanceof ParentFields) {
                ParentFields par = (ParentFields) it;
                if (!par.noOverlap()) nmFail.incrementAndGet();
            }
        });
        if (nmFail.get() == 0) {
            log.info("  * No overlap detected in {}.", getName());
            return true;
        } else {
            log.info("  * Overlap detected in sub-group.");
            return false;
        }
    }

    default boolean validNames() {
        log.info("  * Checking for valid names in group {} ...", getName());
        AtomicInteger countBad = new AtomicInteger();
        getFields().forEach(it -> scanBadNamedField(it, countBad));
        if (countBad.get() == 0) {
            getFields().forEach(it -> scanParentFields(it, countBad));
            if (countBad.get() == 0) {
                log.info("  * No bad fieldName detected in {}.", getName());
                return true;
            } else {
                log.info("  * Bad fieldName detected in sub-group.");
                return false;
            }
        } else {
            log.info("  * {} bad fieldName detected.", countBad.get());
            return false;
        }
    }

    default boolean noDuplicateName() {
        log.info("  * Checking for duplicate in group {} ...", getName());
        Map<String, NamedField> map = new HashMap<>();
        AtomicInteger countDup = new AtomicInteger();
        getFields().forEach(it -> scanNamedField(it, map, countDup));
        if (countDup.get() == 0) {
            getFields().forEach(it -> scanParentFields(it, countDup));
            if (countDup.get() == 0) {
                log.info("  * No duplicate fieldName detected in {}.", getName());
                return true;
            } else {
                log.info("  * Duplicate fieldName detected in sub-group.");
                return false;
            }
        } else {
            log.info("  * {} duplicate fieldName detected.", countDup.get());
            return false;
        }
    }

    default boolean noBadName() {
        log.info("  * Checking for bad name in group {} ...", getName());
        AtomicInteger countBad = new AtomicInteger();
        getFields().forEach(it -> scanBadName(it, countBad));
        if (countBad.get() == 0) {
            getFields().forEach(it -> scanParentFields(it, countBad));
            if (countBad.get() == 0) {
                log.info("  * No bad fieldName detected in {}.", getName());
                return true;
            } else {
                log.info("  * Bad fieldName detected in sub-group.");
                return false;
            }
        } else {
            log.info("  * {} bad fieldName detected.", countBad.get());
            return false;
        }
    }

    default void scanParentFields(NakedField it, AtomicInteger dup) {
        if (it instanceof ParentFields) {
            ParentFields par = (ParentFields) it;
            if (!par.noDuplicateName()) dup.getAndIncrement();
        }
    }

    default void scanBadNamedField(NakedField it, AtomicInteger bad) {
        if (it instanceof NamedField) {
            NamedField kt = (NamedField) it;
            if (!kt.getName().matches("[a-zA-Z_][a-zA-Z0-9]*]")) {
                log.warn("  * FieldName {} not valid @{}+{}", kt.getName(), it.getOffset(), it.getLength());
                bad.getAndIncrement();
            }
        }
    }

    default void scanNamedField(NakedField it, Map<String, NamedField> map, AtomicInteger dup) {
        if (it instanceof NamedField) {
            NamedField kt = (NamedField) it;
            NamedField old = map.put(kt.getName(), kt);
            if (old != null) {
                log.warn("  * FieldName {} duplicate @{}+{} and @{}+{}", kt.getName(),
                    old.getOffset(), old.getLength(), it.getOffset(), it.getLength());
                dup.getAndIncrement();
            }
        }
    }

    default void scanBadName(NakedField it, AtomicInteger dup) {
        if (it instanceof NamedField) {
            NamedField kt = (NamedField) it;
            if (!kt.getName().matches("[a-zA-Z_][a-zA-Z_0-9$]*")) {
                log.warn("  * FieldName {} not valid @{}+{}", kt.getName(), it.getOffset(), it.getLength());
                dup.getAndIncrement();
            }
        }
    }

    default boolean noOverlap() {
        return noOverlap(1);
    }

    default boolean noHole() {
        return noHole(1);
    }

}
