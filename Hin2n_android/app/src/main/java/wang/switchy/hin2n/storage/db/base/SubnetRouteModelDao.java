package wang.switchy.hin2n.storage.db.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

import wang.switchy.hin2n.storage.db.base.model.SubnetRouteModel;

public class SubnetRouteModelDao extends AbstractDao<SubnetRouteModel, Long> {

    public static final String TABLENAME = "SUBNET_ROUTE";

    public static class Properties {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property SettingId = new Property(1, long.class, "settingId", false, "SETTING_ID");
        public static final Property Network = new Property(2, String.class, "network", false, "NETWORK");
        public static final Property PrefixLength = new Property(3, int.class, "prefixLength", false, "PREFIX_LENGTH");
        public static final Property GatewayIp = new Property(4, String.class, "gatewayIp", false, "GATEWAY_IP");
        public static final Property RouteOrder = new Property(5, int.class, "routeOrder", false, "ROUTE_ORDER");
    }

    public SubnetRouteModelDao(DaoConfig config) {
        super(config);
    }

    public SubnetRouteModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"SUBNET_ROUTE\" ("
                + "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + "\"SETTING_ID\" INTEGER NOT NULL ,"
                + "\"NETWORK\" TEXT NOT NULL ,"
                + "\"PREFIX_LENGTH\" INTEGER NOT NULL ,"
                + "\"GATEWAY_IP\" TEXT NOT NULL ,"
                + "\"ROUTE_ORDER\" INTEGER NOT NULL ,"
                + "FOREIGN KEY(\"SETTING_ID\") REFERENCES \"N2NSettingList\"(\"_id\") ON DELETE CASCADE)");
        db.execSQL("CREATE INDEX " + constraint + "\"IDX_SUBNET_ROUTE_SETTING\" "
                + "ON \"SUBNET_ROUTE\" (\"SETTING_ID\", \"ROUTE_ORDER\")");
    }

    public static void dropTable(Database db, boolean ifExists) {
        db.execSQL("DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SUBNET_ROUTE\"");
    }

    @Override
    protected void bindValues(DatabaseStatement statement, SubnetRouteModel entity) {
        statement.clearBindings();
        if (entity.getId() != null) {
            statement.bindLong(1, entity.getId());
        }
        statement.bindLong(2, entity.getSettingId());
        statement.bindString(3, entity.getNetwork());
        statement.bindLong(4, entity.getPrefixLength());
        statement.bindString(5, entity.getGatewayIp());
        statement.bindLong(6, entity.getRouteOrder());
    }

    @Override
    protected void bindValues(SQLiteStatement statement, SubnetRouteModel entity) {
        statement.clearBindings();
        if (entity.getId() != null) {
            statement.bindLong(1, entity.getId());
        }
        statement.bindLong(2, entity.getSettingId());
        statement.bindString(3, entity.getNetwork());
        statement.bindLong(4, entity.getPrefixLength());
        statement.bindString(5, entity.getGatewayIp());
        statement.bindLong(6, entity.getRouteOrder());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }

    @Override
    public SubnetRouteModel readEntity(Cursor cursor, int offset) {
        return new SubnetRouteModel(
                cursor.isNull(offset) ? null : cursor.getLong(offset),
                cursor.getLong(offset + 1),
                cursor.getString(offset + 2),
                cursor.getInt(offset + 3),
                cursor.getString(offset + 4),
                cursor.getInt(offset + 5));
    }

    @Override
    public void readEntity(Cursor cursor, SubnetRouteModel entity, int offset) {
        entity.setId(cursor.isNull(offset) ? null : cursor.getLong(offset));
        entity.setSettingId(cursor.getLong(offset + 1));
        entity.setNetwork(cursor.getString(offset + 2));
        entity.setPrefixLength(cursor.getInt(offset + 3));
        entity.setGatewayIp(cursor.getString(offset + 4));
        entity.setRouteOrder(cursor.getInt(offset + 5));
    }

    @Override
    protected Long updateKeyAfterInsert(SubnetRouteModel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    @Override
    public Long getKey(SubnetRouteModel entity) {
        return entity == null ? null : entity.getId();
    }

    @Override
    public boolean hasKey(SubnetRouteModel entity) {
        return entity.getId() != null;
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
}
