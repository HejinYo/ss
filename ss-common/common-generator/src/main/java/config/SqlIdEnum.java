package config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/11 2:21
 */
@AllArgsConstructor
@Getter
public enum SqlIdEnum {
    /**
     * BaseResultMap
     */
    BASE_RESULT_MAP("BaseResultMap", "实体映射", "BaseResultMap"),
    BASE_COLUMN_LIST("Base_Column_List", "表基本列", "Base_Column_List"),
    BASE_WHERE_SQL("Base_Where_SQL", "通用查询sql", "Base_Where_SQL"),
    SAVE_FULL("insert", "保存一条完整记录", "saveFull"),
    SAVE("insertSelective", "保存一条记录", "save"),
    UPDATE_FULL("updateByPrimaryKey", "更新一条完整记录", "updateFull"),
    UPDATE("updateByPrimaryKeySelective", "更新一条记录", "update"),
    DELETE_BY_PK("deleteByPrimaryKey", "删除给定主键的记录", "deleteByPk"),
    SELECT_BY_PK("selectByPrimaryKey", "主键查找一条记录", "findByPk"),
    SELECT_LIST("findList", "查询列表", "findList"),
    SELECT_PAGE("findPage", "分页查询", "findPage"),
    ;

    private String value;
    private String state;
    private String newValue;

}
