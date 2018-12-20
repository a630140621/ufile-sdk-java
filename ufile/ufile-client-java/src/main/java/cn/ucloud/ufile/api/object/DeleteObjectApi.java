package cn.ucloud.ufile.api.object;

import cn.ucloud.ufile.auth.ObjectAuthorizer;
import cn.ucloud.ufile.auth.ObjectOptAuthParam;
import cn.ucloud.ufile.bean.base.BaseResponseBean;
import cn.ucloud.ufile.exception.UfileException;
import cn.ucloud.ufile.exception.UfileRequiredParamNotFoundException;
import cn.ucloud.ufile.http.HttpClient;
import cn.ucloud.ufile.http.request.DeleteRequestBuilder;
import cn.ucloud.ufile.util.HttpMethod;
import cn.ucloud.ufile.util.ParameterValidator;
import com.google.gson.JsonElement;
import sun.security.validator.ValidatorException;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * API-删除云端对象
 *
 * @author: joshua
 * @E-mail: joshua.yin@ucloud.cn
 * @date: 2018/11/12 19:08
 */
public class DeleteObjectApi extends UfileObjectApi<BaseResponseBean> {
    /**
     * Required
     * 云端对象名称
     */
    @NotEmpty(message = "KeyName is required")
    private String keyName;
    /**
     * Required
     * Bucket空间名称
     */
    @NotEmpty(message = "BucketName is required")
    private String bucketName;

    /**
     * 构造方法
     *
     * @param authorizer Object授权器
     * @param host       API域名
     * @param httpClient Http客户端
     */
    protected DeleteObjectApi(ObjectAuthorizer authorizer, String host, HttpClient httpClient) {
        super(authorizer, host, httpClient);
        RESP_CODE_SUCCESS = 204;
    }

    /**
     * 配置要删除的云端对象名称
     *
     * @param keyName 对象名称
     * @return {@link DeleteObjectApi}
     */
    public DeleteObjectApi keyName(String keyName) {
        this.keyName = keyName;
        return this;
    }

    /**
     * 配置要删除的对象所在Bucket
     *
     * @param bucketName bucket名称
     * @return {@link DeleteObjectApi}
     */
    public DeleteObjectApi atBucket(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    /**
     * 配置签名可选参数
     *
     * @param authOptionalData 签名可选参数
     * @return
     */
    public DeleteObjectApi withAuthOptionalData(JsonElement authOptionalData) {
        this.authOptionalData = authOptionalData;
        return this;
    }

    @Override
    protected void prepareData() throws UfileException {
        try {
            ParameterValidator.validator(this);
            String contentType = "application/json; charset=utf-8";
            String date = dateFormat.format(new Date(System.currentTimeMillis()));

            String authorization = authorizer.authorization((ObjectOptAuthParam) new ObjectOptAuthParam(HttpMethod.DELETE, bucketName, keyName,
                    contentType, "", date).setOptional(authOptionalData));

            call = new DeleteRequestBuilder()
                    .baseUrl(generateFinalHost(bucketName, keyName))
                    .addHeader("Content-Type", contentType)
                    .addHeader("Accpet", "*/*")
                    .addHeader("Date", date)
                    .addHeader("authorization", authorization)
                    .build(httpClient.getOkHttpClient());
        } catch (ValidatorException e) {
            throw new UfileRequiredParamNotFoundException(e.getMessage());
        }
    }
}