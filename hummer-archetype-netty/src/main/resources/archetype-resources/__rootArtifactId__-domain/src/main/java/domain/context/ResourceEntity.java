package ${package}.domain.context;


import ${package}.domain.resource.VenomRateLimiter;
import ${package}.domain.slotchain.ProcessorSlotChain;
import lombok.Data;

@Data
public class ResourceEntity {
   private VenomRateLimiter limiter;
   private ProcessorSlotChain slotChain;
   private String path;
}
