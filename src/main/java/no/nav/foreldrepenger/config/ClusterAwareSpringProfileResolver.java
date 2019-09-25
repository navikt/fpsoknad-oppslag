package no.nav.foreldrepenger.config;

import static java.lang.System.getenv;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.DEFAULT;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.LOCAL;

import java.util.Optional;

public class ClusterAwareSpringProfileResolver {

    private static final String NAIS_CLUSTER_NAME = "NAIS_CLUSTER_NAME";

    public static String[] profiles() {
        return Optional.ofNullable(clusterFra(getenv(NAIS_CLUSTER_NAME)))
                .map(c -> new String[] { c })
                .orElse(new String[0]);
    }

    private static String clusterFra(String cluster) {
        if (cluster == null) {
            return LOCAL;
        }
        if (cluster.contains(DEV)) {
            return DEV;
        }
        return DEFAULT;
    }
}
