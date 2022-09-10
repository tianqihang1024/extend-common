package extend.constants;

/**
 * @author 田奇杭
 * @Description 秒杀消息的Topic定义
 * @Date 2022/8/28 19:56
 */
public interface SeckillTopicConstant {

    /**
     * 秒杀商品变更Topic
     */
    String SECKILL_COMMODITY_CHANGE_TOPIC = "seckill_commodity_change_topic";

    /**
     * 秒杀商品库存扣减Topic
     */
    String SECKILL_COMMODITY_STOCK_DEDUCTION_TOPIC = "seckill_commodity_stock_deduction_topic";


}
