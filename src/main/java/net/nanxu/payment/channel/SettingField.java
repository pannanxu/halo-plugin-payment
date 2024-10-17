package net.nanxu.payment.channel;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * SettingField.
 *
 * @author: P
 **/
@Data
public class SettingField {
    // 属性名
    private String name;
    // 显示标签
    private String label;
    // 表单类型: text、number、select、radio、checkbox、textarea、file
    private String type;
    // 占位符
    private String placeholder;
    // 默认值
    private String defaultValue;
    // 帮助，在表单下方显示
    private String help;
    // 是否必须
    private Boolean required;
    // 是否安全，如密码不希望被网页中看到
    private Boolean security;
    // 选项参数，如果是select、radio等类型，则该参数为选项值
    private List<Option> options;

    @Data
    public static class Option {
        private String label;
        private String value;
    }

    public static SettingField text(String name, String label) {
        return create(name, label, "text");
    }
    
    public static SettingField number(String name, String label) {
        return create(name, label, "number");
    }
    
    public static SettingField select(String name, String label) {
        return create(name, label, "select");
    }
    
    public static SettingField radio(String name, String label) {
        return create(name, label, "radio");
    }
    
    public static SettingField checkbox(String name, String label) {
        return create(name, label, "checkbox");
    }
    
    public static SettingField textarea(String name, String label) {
        return create(name, label, "textarea");
    }
    
    public static SettingField file(String name, String label) {
        return create(name, label, "file");
    }
    
    public static SettingField password(String name, String label) {
        return create(name, label, "password");
    }
    
    public static SettingField email(String name, String label) {
        return create(name, label, "email");
    }
    
    public static SettingField url(String name, String label) {
        return create(name, label, "url");
    }
    
    
    
    public static SettingField create(String name, String label, String type) {
        SettingField field = new SettingField();
        field.setName(name);
        field.setLabel(label);
        field.setType(type);
        return field;
    }

    public SettingField required() {
        this.setRequired(true);
        return this;
    }

    public SettingField security() {
        this.setSecurity(true);
        return this;
    }

    public SettingField placeholder(String placeholder) {
        this.setPlaceholder(placeholder);
        return this;
    }

    public SettingField defaultValue(String defaultValue) {
        this.setDefaultValue(defaultValue);
        return this;
    }

    public SettingField help(String help) {
        this.setHelp(help);
        return this;
    }

    public SettingField options(List<Option> options) {
        this.setOptions(options);
        return this;
    }

    public SettingField option(String label, String value) {
        Option option = new Option();
        option.setLabel(label);
        option.setValue(value);
        if (null == this.getOptions()) {
            this.setOptions(new ArrayList<>());
        }
        this.getOptions().add(option);
        return this;
    }
}
