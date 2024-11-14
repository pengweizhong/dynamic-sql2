package com.pengwz.dynamic.sql2.core.dml.select;

import java.util.List;
import java.util.Map;

public interface SelectHandler {

    List<Map<String, Object>> executeQuery();
}
