package youxianqi.yixi.oss;

import lombok.Data;

@Data
public class ResponsePostPolicy {
        private String accessId;
        private String policy;
        private String signature;
        private String callback;
        private String host;
        private String dir;
        private int expires;
}
