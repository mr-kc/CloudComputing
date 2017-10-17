import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

/**
 * This is a Java class that will create an instance in AWS using
 * the credentials stored in "AwsCredentials.properties" file.
 * 
 * Full documentation on the API can be found in: 
 * 
 * http://docs.amazonwebservices.com/AWSSdkDocsJava/latest/DeveloperGuide/init-ec2-client.html
 * 
 * San Jose State University - Fall 2015 CS218
 *
 */

public class CS218LaunchEC2Instance 
{

	public static AWSCredentials credentials;
	public static AmazonEC2Client amazonEC2Client;
	
	public static void main(String[] args) 
	{
		String sshKeyName = "your-key-name";
		
        importCredentials();
        createEC2Object();
        createSecurityGroup("CS218JavaSecurityGroup","My CS218 Java Security Group" );
        runAWSInstance(sshKeyName, "CS218JavaSecurityGroup");
	} 

	private static void runAWSInstance(String keyName, String securityGroupName) 
	{
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
        	        	
        	  
        runInstancesRequest.withImageId("ami-fe002cbb")
              					.withInstanceType("t1.micro")
        	                    .withMinCount(1)
        	                    .withMaxCount(1)
        	                    .withKeyName(keyName)
        	                    .withSecurityGroups(securityGroupName);
        
        RunInstancesResult runInstancesResult = amazonEC2Client
        		.runInstances(runInstancesRequest);
        System.out.println("Result of starting instance: " + runInstancesResult);
	}
	
	/**
	 * This programmatically creates an AWS Security group with TCP port 22 (SSH)
	 * open.
	 * 
	 * @param groupName Name of AWS Security Group to be created
	 * @param groupDescription Description of AWS Security Group to be created.
	 */

	private static void createSecurityGroup(String groupName, String groupDescription) 
	{
		CreateSecurityGroupRequest createSecurityGroupRequest = 
				new CreateSecurityGroupRequest();        	
        createSecurityGroupRequest.withGroupName(groupName)
        						  .withDescription(groupDescription);
        
        CreateSecurityGroupResult createSecurityGroupResult = 
        		amazonEC2Client.createSecurityGroup(createSecurityGroupRequest);
        
        System.out.println("Result of creating the security group:" + createSecurityGroupResult);
        
        IpPermission ipPermission = new IpPermission();
        	    	
        ipPermission.withIpProtocol("tcp")
        			.withIpRanges("0.0.0.0/0")
        			.withFromPort(22)
        	        .withToPort(22);
        
        AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = 
        		new AuthorizeSecurityGroupIngressRequest();	    	
        	
        authorizeSecurityGroupIngressRequest.withGroupName(groupName)
        	                                .withIpPermissions(ipPermission);
        	
        amazonEC2Client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
	}
	
	/**
	 * Creates an "AmazonEC2Client" object that interacts with 
	 * AWS to create instances.
	 * 
	 * Note that we have hard coded it to talk to the amazon US East region.
	 */

	private static void createEC2Object() 
	{
		amazonEC2Client = new AmazonEC2Client(credentials);
        amazonEC2Client.setEndpoint("ec2.us-west-1.amazonaws.com");
	}
	
	/**
	 * Imports the credentials stored in "AwsCredentials.properties" file into 
	 * a Java Object.
	 */

	private static void importCredentials() 
	{
		try 
        {
			credentials = new PropertiesCredentials(CS218LaunchEC2Instance
					.class.getResourceAsStream("AwsCredentials.properties"));
		} 
        
        catch (IOException e) 
        {
			e.printStackTrace();
		}
	}
	
}
