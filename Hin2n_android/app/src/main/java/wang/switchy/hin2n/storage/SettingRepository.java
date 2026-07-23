package wang.switchy.hin2n.storage;

import java.util.ArrayList;
import java.util.List;

import wang.switchy.hin2n.model.N2NSettingInfo;
import wang.switchy.hin2n.model.SubnetRoute;
import wang.switchy.hin2n.storage.db.base.DaoSession;
import wang.switchy.hin2n.storage.db.base.SubnetRouteModelDao;
import wang.switchy.hin2n.storage.db.base.model.N2NSettingModel;
import wang.switchy.hin2n.storage.db.base.model.SubnetRouteModel;

public final class SettingRepository {

    private final DaoSession daoSession;

    public SettingRepository(DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public N2NSettingInfo toSettingInfo(N2NSettingModel setting) {
        if (setting == null || setting.getId() == null) {
            throw new IllegalArgumentException("Saved setting is required");
        }
        return new N2NSettingInfo(setting, loadSubnetRoutes(setting.getId()));
    }

    public List<SubnetRoute> loadSubnetRoutes(long settingId) {
        List<SubnetRouteModel> models = daoSession.getSubnetRouteModelDao()
                .queryBuilder()
                .where(SubnetRouteModelDao.Properties.SettingId.eq(settingId))
                .orderAsc(SubnetRouteModelDao.Properties.RouteOrder)
                .list();
        List<SubnetRoute> routes = new ArrayList<>(models.size());
        for (SubnetRouteModel model : models) {
            routes.add(new SubnetRoute(model.getNetwork(), model.getPrefixLength(), model.getGatewayIp()));
        }
        return routes;
    }

    public void insertSetting(N2NSettingModel setting, List<SubnetRoute> routes) {
        daoSession.runInTx(() -> {
            long settingId = daoSession.getN2NSettingModelDao().insert(setting);
            insertRoutes(settingId, routes);
        });
    }

    public void updateSetting(N2NSettingModel setting, List<SubnetRoute> routes) {
        if (setting.getId() == null) {
            throw new IllegalArgumentException("Saved setting is required");
        }
        daoSession.runInTx(() -> {
            daoSession.getN2NSettingModelDao().update(setting);
            deleteRoutes(setting.getId());
            insertRoutes(setting.getId(), routes);
        });
    }

    public void deleteSetting(long settingId) {
        daoSession.runInTx(() -> {
            deleteRoutes(settingId);
            daoSession.getN2NSettingModelDao().deleteByKey(settingId);
        });
    }

    private void insertRoutes(long settingId, List<SubnetRoute> routes) {
        if (routes == null || routes.isEmpty()) {
            return;
        }
        List<SubnetRouteModel> models = new ArrayList<>(routes.size());
        for (int index = 0; index < routes.size(); index++) {
            SubnetRoute route = routes.get(index);
            models.add(new SubnetRouteModel(null, settingId, route.getNetwork(),
                    route.getPrefixLength(), route.getGatewayIp(), index));
        }
        daoSession.getSubnetRouteModelDao().insertInTx(models);
    }

    private void deleteRoutes(long settingId) {
        List<SubnetRouteModel> routes = daoSession.getSubnetRouteModelDao()
                .queryBuilder()
                .where(SubnetRouteModelDao.Properties.SettingId.eq(settingId))
                .list();
        if (!routes.isEmpty()) {
            daoSession.getSubnetRouteModelDao().deleteInTx(routes);
        }
    }
}
