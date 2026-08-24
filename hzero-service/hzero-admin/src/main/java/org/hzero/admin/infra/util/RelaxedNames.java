package org.hzero.admin.infra.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * RelaxedNames
 * </p>
 *
 * @author modify by bo.he02@hand-china.com on 2020/05/12
 */
public final class RelaxedNames implements Iterable<String> {

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([^A-Z-])([A-Z])");
    private static final Pattern SEPARATED_TO_CAMEL_CASE_PATTERN = Pattern.compile("[_\\-.]");

    private final String name;
    private final Set<String> values = new LinkedHashSet<>();

    /**
     * Create a new {@link RelaxedNames} instance.
     *
     * @param name the source name. For the maximum number of variations specify the name
     *             using dashed notation (e.g. {@literal my-property-name}
     */
    public RelaxedNames(String name) {
        this.name = (name == null ? "" : name);
        initialize(RelaxedNames.this.name, this.values);
    }

    /**
     * Return a {@link RelaxedNames} for the given source camelCase source name.
     *
     * @param name the source name in camelCase
     * @return the relaxed names
     */
    public static RelaxedNames forCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        for (char c : name.toCharArray()) {
            result.append(Character.isUpperCase(c) && result.length() > 0
                    && result.charAt(result.length() - 1) != '-'
                    ? "-" + Character.toLowerCase(c) : c);
        }
        return new RelaxedNames(result.toString());
    }

    @Override
    public Iterator<String> iterator() {
        return this.values.iterator();
    }

    private void initialize(String name, Set<String> values) {
        if (values.contains(name)) {
            return;
        }
        for (Variation variation : Variation.values()) {
            for (Manipulation manipulation : Manipulation.values()) {
                String result = name;
                result = manipulation.apply(result);
                result = variation.apply(result);
                values.add(result);
                initialize(result, values);
            }
        }
    }

    /**
     * Name variations.
     */
    enum Variation {
        /**
         * 不处理
         */
        NONE {
            @Override
            public String apply(String value) {
                return value;
            }
        },
        /**
         * 转为小写
         */
        LOWERCASE {
            @Override
            public String apply(String value) {
                return value.isEmpty() ? value : value.toLowerCase();
            }
        },
        /**
         * 转为大写
         */
        UPPERCASE {
            @Override
            public String apply(String value) {
                return value.isEmpty() ? value : value.toUpperCase();
            }
        };

        /**
         * 处理方法
         *
         * @param value 值
         * @return 结果
         */
        public abstract String apply(String value);
    }

    /**
     * Name manipulations.
     */
    enum Manipulation {
        /**
         * 不处理
         */
        NONE {
            @Override
            public String apply(String value) {
                return value;
            }

        },
        /**
         * 中划线转换成下划线
         */
        HYPHEN_TO_UNDERSCORE {
            @Override
            public String apply(String value) {
                return value.indexOf('-') != -1 ? value.replace("-", "_") : value;
            }

        },
        /**
         * 下划线转换成点
         */
        UNDERSCORE_TO_PERIOD {
            @Override
            public String apply(String value) {
                return value.indexOf('_') != -1 ? value.replace("_", ".") : value;
            }

        },
        /**
         * 点转换成下划线
         */
        PERIOD_TO_UNDERSCORE {
            @Override
            public String apply(String value) {
                return value.indexOf('.') != -1 ? value.replace(".", "_") : value;
            }
        },
        /**
         * 驼峰转为下划线
         */
        CAMELCASE_TO_UNDERSCORE {
            @Override
            public String apply(String value) {
                if (value.isEmpty()) {
                    return value;
                }
                Matcher matcher = CAMEL_CASE_PATTERN.matcher(value);
                if (!matcher.find()) {
                    return value;
                }
                matcher.reset();
                StringBuffer result = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(result, matcher.group(1) + '_'
                            + StringUtils.uncapitalize(matcher.group(2)));
                }
                matcher.appendTail(result);
                return result.toString();
            }

        },
        /**
         * 驼峰转换成中划线
         */
        CAMELCASE_TO_HYPHEN {
            @Override
            public String apply(String value) {
                if (value.isEmpty()) {
                    return value;
                }
                Matcher matcher = CAMEL_CASE_PATTERN.matcher(value);
                if (!matcher.find()) {
                    return value;
                }
                matcher.reset();
                StringBuffer result = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(result, matcher.group(1) + '-'
                            + StringUtils.uncapitalize(matcher.group(2)));
                }
                matcher.appendTail(result);
                return result.toString();
            }

        },
        /**
         * 转换成驼峰，区分大小写
         */
        SEPARATED_TO_CAMELCASE {
            @Override
            public String apply(String value) {
                return separatedToCamelCase(value, false);
            }

        },
        /**
         * 转换成驼峰，不区分大小写
         */
        CASE_INSENSITIVE_SEPARATED_TO_CAMELCASE {
            @Override
            public String apply(String value) {
                return separatedToCamelCase(value, true);
            }

        };

        private static final char[] SUFFIXES = new char[]{'_', '-', '.'};

        /**
         * 转换成驼峰
         *
         * @param value           值
         * @param caseInsensitive 是否区分大小写
         * @return 结果
         */
        private static String separatedToCamelCase(String value, boolean caseInsensitive) {
            if (value.isEmpty()) {
                return value;
            }
            StringBuilder builder = new StringBuilder();
            for (String field : SEPARATED_TO_CAMEL_CASE_PATTERN.split(value)) {
                field = (caseInsensitive ? field.toLowerCase() : field);
                builder.append(
                        builder.length() == 0 ? field : StringUtils.capitalize(field));
            }
            char lastChar = value.charAt(value.length() - 1);
            for (char suffix : SUFFIXES) {
                if (lastChar == suffix) {
                    builder.append(suffix);
                    break;
                }
            }
            return builder.toString();
        }

        /**
         * 处理方法
         *
         * @param value 值
         * @return 结果
         */
        public abstract String apply(String value);
    }
}