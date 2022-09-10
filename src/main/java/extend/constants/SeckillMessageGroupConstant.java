package extend.constants;

/**
 * @author 田奇杭
 * @Description 秒杀消息的ConsumerGroup定义
 * @Date 2022/8/28 19:56
 */
public interface SeckillMessageGroupConstant {

    /**
     * 秒杀商品变更消息分组
     */
    String SECKILL_COMMODITY_CHANGE_GROUP = "seckill_commodity_change_group";

    /**
     * 秒杀商品库存扣减消息分组
     */
    String SECKILL_COMMODITY_STOCK_DEDUCTION_GROUP = "seckill_commodity_stock_deduction_group";

}
