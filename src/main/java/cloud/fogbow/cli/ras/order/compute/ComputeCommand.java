package cloud.fogbow.cli.ras.order.compute;

import cloud.fogbow.cli.FogbowCliHttpUtil;
import cloud.fogbow.cli.constants.Documentation;
import cloud.fogbow.cli.constants.Messages;
import cloud.fogbow.cli.exceptions.FogbowCLIException;
import cloud.fogbow.cli.fns.compute.ComputeWrappedWithFedNet;
import cloud.fogbow.cli.ras.order.OrderCommand;
import cloud.fogbow.cli.utils.CommandUtil;
import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.util.CloudInitUserDataBuilder;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@Parameters(separators = "=", commandDescription = "CompunetworkIdste manipulation")
public class ComputeCommand {
    public static final String ALLOCATION_ENDPOINT_KEY = "/allocation/";
    public static final String CLOUD_NAME_KEY = "cloudName";
    public static final String COMPUTE_ORDER_JSON_KEY = "compute";
    public static final String ENDPOINT = '/' + "computes";
    public static final String GET_QUOTA_COMMAND_KEY = "--get-quota";
    public static final String GET_ALLOCATION_COMMAND_KEY = "--get-allocation";
    public static final String NAME = "compute";
    public static final String NAME_KEY = "name";
    public static final String NETWORK_IDS_KEY = "networkIds";
    public static final String PROVIDER_KEY = "provider";
    public static final String PUBLIC_KEY_KEY = "publicKey";
    public static final String QUOTA_ENDPOINT_KEY = "/quota/";
    public static final String USER_DATA_KEY = "userData";
    public static final String USER_DATA_SEPARATOR = ":";
    public static final String USER_DATAFILE_CONTENT_KEY = "extraUserDataFileContent";
    public static final String USER_DATAfILE_FILE_TYPE_KEY = "extraUserDataFileType";
    public static final String USER_DATAfILE_TAG_KEY = "tag";

    public static final String VCPU_KEY = "vCPU";
    public static final String MEMORY_KEY = "memory";
    public static final String DISK_KEY = "disk";
    public static final String IMAGE_ID_KEY = "imageId";
    public static final String REQUIREMENTS_KEY = "requirements";
    public static final String DEFAULT_PUBLIC_KEY_PATH_FILE = "~/.ssh/id_rsa.pub";


    @Parameter(names = GET_QUOTA_COMMAND_KEY, description = Documentation.Quota.GET)
    private Boolean isGetQuotaCommand = false;

    @Parameter(names = GET_ALLOCATION_COMMAND_KEY, description = Documentation.Allocation.GET)
    private Boolean isGetAllocationCommand = false;

    @ParametersDelegate
    private ComputeWrappedWithFedNet compute = new ComputeWrappedWithFedNet();

    @ParametersDelegate
    private OrderCommand orderCommand = new OrderCommand(ENDPOINT, this.compute);

    public String run() throws FogbowException, FogbowCLIException {
        if (this.orderCommand.getIsCreateCommand()) {
            return doCreate();
        } else if (this.orderCommand.getIsDeleteCommand()) {
            return this.orderCommand.doDelete();
        } else if (this.orderCommand.getIsGetCommand()) {
            return this.orderCommand.doGet();
        } else if (this.orderCommand.getIsGetAllCommand()) {
            return this.orderCommand.doGetAll();
        } else if (this.isGetQuotaCommand) {
            return doGetQuota();
        } else if (this.isGetAllocationCommand) {
            return doGetAllocation();
        }
        throw new ParameterException(Messages.Exception.INCOMPLETE_COMMAND);
    }

    private String doCreate() throws FogbowException, FogbowCLIException {
        HashMap computeOrder = null;

        if(compute.getFederatedNetworkId() != null){
            computeOrder = compute.getHTTPHashMap();
            computeOrder.put(COMPUTE_ORDER_JSON_KEY, getNormalComputer());
        } else {
            computeOrder = getNormalComputer();
        }

        FogbowCliHttpUtil fogbowCliHttpUtil = orderCommand.getFogbowCliHttpUtil();
        String createComputeResponse = fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.POST, ENDPOINT, computeOrder);

        return createComputeResponse;
    }

    private HashMap getNormalComputer() throws FogbowException, FogbowCLIException {
        HashMap body = new HashMap();
        ComputeWrappedWithFedNet simpleCompute = compute;

        HashMap requiredParams = getRequiredParams(simpleCompute);
        HashMap optionalParams = getOptionalParams(simpleCompute);

        CommandUtil.extendMap(body, requiredParams);
        CommandUtil.extendMap(body, optionalParams);

        return body;
    }

    private HashMap getRequiredParams(Compute simpleCompute) throws InvalidParameterException {
        HashMap body = new HashMap();

        String vCpu = simpleCompute.getvCPU();
        String memory = simpleCompute.getMemory();
        String disk = simpleCompute.getDisk();
        String imageId = simpleCompute.getImageId();

        checkComputeValues(vCpu, memory, disk, imageId);

        body.put(VCPU_KEY, vCpu);
        body.put(MEMORY_KEY, memory);
        body.put(DISK_KEY, disk);
        body.put(IMAGE_ID_KEY, imageId);

        return body;
    }

    private HashMap getOptionalParams(ComputeWrappedWithFedNet simpleCompute) throws InvalidParameterException, FogbowCLIException {
        HashMap body = new HashMap();

        body.put(CLOUD_NAME_KEY, orderCommand.getCloudName());
        body.put(NETWORK_IDS_KEY, simpleCompute.getNetworkIds());
        body.put(NAME_KEY, simpleCompute.getName());
        body.put(PUBLIC_KEY_KEY, getPublicKeyFileContent(simpleCompute));
        body.put(PROVIDER_KEY, orderCommand.getMemberId());
        body.put(USER_DATA_KEY, getUserDataList(simpleCompute));
        body.put(REQUIREMENTS_KEY, simpleCompute.getRequirements());

        return body;
    }

    private String doGetAllocation() throws FogbowException {
        if (this.orderCommand.getMemberId() == null || this.orderCommand.getCloudName() == null) {
            throw new ParameterException(Messages.Exception.NO_MEMBER_ID_OR_CLOUD_NAME);
        } else {
            String fullPath = ENDPOINT + ALLOCATION_ENDPOINT_KEY + this.orderCommand.getMemberId() +
                    "/" + this.orderCommand.getCloudName();
            FogbowCliHttpUtil fogbowCliHttpUtil = orderCommand.getFogbowCliHttpUtil();
            return fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.GET, fullPath);

        }
    }

    private String doGetQuota() throws FogbowException {
        if (this.orderCommand.getMemberId() == null || this.orderCommand.getCloudName() == null) {
            throw new ParameterException(Messages.Exception.NO_MEMBER_ID_OR_CLOUD_NAME);
        } else {
            String fullPath = ENDPOINT + QUOTA_ENDPOINT_KEY + this.orderCommand.getMemberId() +
                    "/" + this.orderCommand.getCloudName();

            FogbowCliHttpUtil fogbowCliHttpUtil = orderCommand.getFogbowCliHttpUtil();
            return fogbowCliHttpUtil.doGenericAuthenticatedRequest(HttpMethod.GET, fullPath);
        }
    }

    private String getUserDataFileType(String fileType) throws FogbowCLIException {
        try {
            CloudInitUserDataBuilder.FileType.valueOf(fileType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FogbowCLIException(Messages.Exception.INCONSISTENT_PARAMS);
        }
        return fileType;
    }

    private void checkComputeValues(String vCpu, String memory, String disk, String imageId) throws InvalidParameterException {
        try {
            Preconditions.checkNotNull(vCpu);
            Preconditions.checkNotNull(memory);
            Preconditions.checkNotNull(disk);
            Preconditions.checkNotNull(imageId);
        } catch (NullPointerException e) {
            throw new InvalidParameterException(Messages.Exception.MISSING_CREATE_COMPUTE_PARAMS);
        }
    }

    private List getUserDataList(Compute simpleCompute) throws InvalidParameterException, FogbowCLIException {
        List userDataList = new ArrayList();

        List<String> userDataValues = simpleCompute.getUserData();
        if (userDataValues != null) {
            for (int i = 0; i < userDataValues.size(); i++) {
                String userData = userDataValues.get(i);
                String data[] = userData.split(USER_DATA_SEPARATOR);

                if (data.length != 2) {
                    throw new InvalidParameterException(Messages.Exception.MALFORMED_USER_DATA);
                }

                String filePath = data[0];
                String fileFormat = data[1];

                String userDataFileContent = CommandUtil.getFileContent(filePath, Messages.Exception.NO_USER_DATA);
                String fileType = getUserDataFileType(fileFormat);

                String encodedUserDataFileContent = Base64.getEncoder().encodeToString(userDataFileContent.getBytes());
                String tag = getFileName(filePath);

                HashMap userDataMap = new HashMap<String, String>();
                userDataMap.put(USER_DATAFILE_CONTENT_KEY, encodedUserDataFileContent);
                userDataMap.put(USER_DATAfILE_FILE_TYPE_KEY, fileType);
                userDataMap.put(USER_DATAfILE_TAG_KEY, tag);

                userDataList.add(userDataMap);
            }
        }

        return userDataList;
    }

    private String getPublicKeyFileContent(Compute simpleCompute) {
        String providedPath = simpleCompute.getPublicKeyPath();

        String publicKeyContent = null;

        try {
            publicKeyContent = CommandUtil.getFileContent(providedPath, Messages.Exception.PUBLIC_KEY_FILE_NOT_FOUND);
        } catch (FogbowCLIException e){
            try {
                publicKeyContent = CommandUtil.getFileContent(DEFAULT_PUBLIC_KEY_PATH_FILE, Messages.Exception.PUBLIC_KEY_FILE_NOT_FOUND);
            } catch (FogbowCLIException f){
                e.printStackTrace();
            }
        }

        return publicKeyContent;
    }

    private String getFileName(String path) {
        String[] paths = path.split("/");
        return paths[paths.length - 1];
    }
}


