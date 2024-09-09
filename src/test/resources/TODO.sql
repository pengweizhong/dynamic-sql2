-- 1、把这种SQL实现一下：
SELECT
    CASE
        WHEN current_month_ret > 10 THEN '>10%'
        WHEN current_month_ret BETWEEN 5 AND 10 THEN '5~10%'
        WHEN current_month_ret BETWEEN 0 AND 5 THEN '0~5%'
        WHEN current_month_ret = 0 THEN '0%'
        WHEN current_month_ret BETWEEN -5 AND 0 THEN '0~-5%'
        WHEN current_month_ret BETWEEN -10 AND -5 THEN '-5~-10%'
        WHEN current_month_ret < -10 THEN '<-10%'
        END AS profit_range,
    COUNT(*) AS product_count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM t_pro_ret_phased), 2) AS percentage
FROM t_pro_ret_phased where current_month_ret is not null
GROUP BY profit_range
ORDER BY FIELD(profit_range, '>10%', '5~10%', '0~5%', '0%', '0~-5%', '-5~-10%', '<-10%');

-- 查出的结果：
-- profit_range|product_count|percentage|
----------+-------------+----------+
-- 0~5%        |         4150|     43.56|
-- 0~-5%       |         5372|     56.39|



-- 2、
select x.product_id,
       sum(x.cnfm_amt) as amount
from trumgu_customer_db.t_cus_transaction_record x
where x.user_id = #{userId}
          and x.trans_type in (0, 1)
          and x.cnfm_date > #{startCnfmDate,jdbcType=DATE}
group by product_id
order by amount desc limit #{displayNumber}

