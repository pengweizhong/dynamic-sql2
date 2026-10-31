# 空间函数实施进度

本表对应最初建议的函数类别。所有类别的基础函数现已实现，并有 MySQL 实库断言；这不表示穷尽 MySQL 全部同义函数、可选参数和输入组合，也不表示其他数据库已经适配。

| 范围 | 已实现及验证 |
| --- | --- |
| 经纬度与 X/Y | 读取及设置坐标分量，地理轴序测试 |
| 空间关系 | Contains、Within、Intersects、Disjoint、Equals、Touches、Crosses、Overlaps；命中、未命中、NULL、SRID 冲突 |
| 距离 | Distance 双表达式与单位、DistanceSphere 双表达式与半径、Point 兼容入口 |
| 面积及属性 | Area、Length、GeometryType、Dimension、IsEmpty、IsValid、IsSimple、IsClosed |
| 格式转换 | GeomFromText、GeomFromWKB、GeomFromGeoJSON、AsText、AsBinary、AsGeoJSON |
| SRID | 读取、表达式与字段设置；设置不改变坐标 |
| 几何变换 | Centroid、Envelope、ConvexHull、SwapXY、Transform |
| 几何运算 | Buffer、BufferStrategy、Intersection、Union、Difference、SymDifference、MakeEnvelope |
| 组成访问 | StartPoint、EndPoint、PointN、NumPoints、ExteriorRing、InteriorRingN、NumInteriorRing、GeometryN、NumGeometries |
| MBR | Contains、CoveredBy、Covers、Disjoint、Equals、Intersects、Overlaps、Touches、Within 全部九种 |
| GeoHash | GeoHash、LatFromGeoHash、LongFromGeoHash、PointFromGeoHash |
| 高级处理 | Simplify、Validate、PointAtDistance、LineInterpolatePoint、LineInterpolatePoints、FrechetDistance、HausdorffDistance |

## 当前验证结果

默认客户端预编译、UTC JVM，以及服务端预编译、Asia/Shanghai JVM：完整 mysql-it 均为 **292 项通过**，包括 MySQL 集成类 275 项、无需数据库测试 17 项，零失败、零跳过。扩展矩阵逐函数与独立 JDBC 常量查询比较，并重复执行同一对象检查绑定稳定性。单元测试额外检查恶意文本绑定、嵌套参数顺序和不支持方言的拒绝行为。

这些数字包括其他业务测试，不能作为空间函数覆盖百分比。新增函数范围以本表为准；未新增 ST_Collect 聚合、专用几何构造同义函数及所有可选参数重载。空间函数返回二进制几何时，调用方需选择合适的映射，不能将任意几何结果直接映射成 Point。

## lineValue 支持边界

lineValue 仍为 byte[]，已验证 MySQL 内部空间二进制的 CRUD 与 Length。AsBinary → 标准 WKB → GeomFromWKB → Length 查询链路已验证。标准 WKB 需通过构造函数转为数据库几何，不能直接当作内部格式写入 lineValue；Java LineString 模型尚未实现。

## 跨数据库适配

当前新增实现仅支持 MySQL。Oracle、DB2 及国产数据库必须提供方言渲染与实库验证，不能仅替换函数名前缀。函数清单参考 [MySQL 8.4 官方手册](https://dev.mysql.com/doc/refman/8.4/en/spatial-function-reference.html)。
