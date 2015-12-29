/*
 * Created on 2005-7-27
 *
 */
package adultadmin.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;

import org.apache.struts.upload.FormFile;

import com.jspsmart.upload.SmartUploadException;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author lbj
 *  
 */
public class FileUtil {
    public static boolean uploadFile(FormFile file, String filePath,
            String fileURL) {

        try {
            File path = new File(filePath);
            if (!path.exists()) {
                path.mkdirs();
            }            
            filePath += "/" + fileURL;
            //retrieve the file data
            InputStream stream = file.getInputStream();

            //write the file to the file specified
            OutputStream bos = new FileOutputStream(filePath);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.close();

            stream.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return false;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }

        file.destroy();
        return true;
    }
       public static String getFileExt(String fileName) {
        if (fileName == null) {
            return "";
        }
        int i = fileName.lastIndexOf(".");
        if (i == -1) {
            return "";
        }
        return fileName.substring(i + 1, fileName.length());
    }

    private static int fileSerial = 10; 
    
    public static String getUniqueFileName() {
    	fileSerial++;
    	if(fileSerial > 99)
    		fileSerial = 10;
        return String.valueOf(Calendar.getInstance().getTimeInMillis() + fileSerial);
    }

    public static boolean checkIconFileExt(String fileExt) {
        if (!fileExt.equalsIgnoreCase("gif")
                && !fileExt.equalsIgnoreCase("jpg")
                && !fileExt.equalsIgnoreCase("png")
                && !fileExt.equalsIgnoreCase("wbmp")
                && !fileExt.equalsIgnoreCase("dgif")) {
            return false;
        }
        return true;
    }

    public static boolean checkImageFileExt(String fileExt) {
        if (!fileExt.equalsIgnoreCase("gif")
                && !fileExt.equalsIgnoreCase("jpg")
                && !fileExt.equalsIgnoreCase("png")
                && !fileExt.equalsIgnoreCase("wbmp")
                && !fileExt.equalsIgnoreCase("dgif")) {
            return false;
        }
        return true;
    }

    public static boolean checkRingFileExt(String fileExt) {
        if (!fileExt.equalsIgnoreCase("mid")
                && !fileExt.equalsIgnoreCase("amr")
                && !fileExt.equalsIgnoreCase("mp3")
                && !fileExt.equalsIgnoreCase("mmf")) {
            return false;
        }
        return true;
    }

    public static boolean checkSoftwareFileExt(String fileExt) {
        if (!fileExt.equalsIgnoreCase("jar")
                && !fileExt.equalsIgnoreCase("jad")) {
            return false;
        }
        return true;
    }

    public static boolean checkVideoFileExt(String fileExt) {
        if (!fileExt.equalsIgnoreCase("jar")
                && !fileExt.equalsIgnoreCase("jad")) {
            return false;
        }
        return true;
    }

    public static void dealImage(String inURL, int newWidth, String outURL) {
        try {
            File _file = new File(inURL);
            //构造Image对象
            Image src = javax.imageio.ImageIO.read(_file);
            //得到源图宽
            int width = src.getWidth(null);
            //得到源图长
            int height = src.getHeight(null);

            BufferedImage tag = new BufferedImage(newWidth, height * newWidth
                    / width, BufferedImage.TYPE_INT_RGB);
            //绘制缩小后的图
            tag.getGraphics().drawImage(src, 0, 0, newWidth,
                    height * newWidth / width, null);

            //输出到文件流
            FileOutputStream out = new FileOutputStream(outURL);

            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ImageFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getThumbnailName(String imageName) {
        if (imageName == null) {
            return null;
        } else {
            imageName = imageName.substring(0, imageName.lastIndexOf('.') - 1)
                    + "_tn.jpg";
            return imageName;
        }
    }

    public static boolean copyFile(File fromFile, File toFile) {
        try {
            if (!toFile.exists()) {
                toFile.createNewFile();
            }

            FileInputStream fis = new FileInputStream(fromFile);
            FileOutputStream fos = new FileOutputStream(toFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) > -1) {
                fos.write(buf, 0, len);
            }
            fis.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean copyDir(File fromDir, File toDir) {
        if (!fromDir.exists()) {
            return false;
        }

        if (!toDir.exists()) {
            toDir.mkdirs();
        }

        File[] fromFiles = fromDir.listFiles();
        if (fromFiles == null) {
            return true;
        }
        File fromFile = null;
        File toFile = null;

        int len = fromFiles.length;
        for (int i = 0; i < len; i++) {
            fromFile = fromFiles[i];
            toFile = new File(toDir.getAbsolutePath() + "/"
                    + fromFile.getName());
            if (!copyFile(fromFile, toFile)) {
                return false;
            }
        }

        return true;
    }
    
    
    public static String uploadFile(com.jspsmart.upload.File file, String filePath) {
    	String name = String.valueOf(System.currentTimeMillis());
    	String dir = name.substring(0, 3) + "/";
    	name = name.substring(3) + "." + getFileExt(file.getFileName());
    	filePath += dir;
    	File path = new File(filePath);
    	if (!path.exists()) {
    		path.mkdirs();
    	}
		try {
			file.saveAs(filePath + name);
			return dir + name;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SmartUploadException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static String uploadFile(FormFile file, String filePath) {
    	if(filePath==null) return null;
    	String name = String.valueOf(System.currentTimeMillis());
    	String dir = name.substring(0, 3) + "/";
    	if(!filePath.endsWith("/")){
    		filePath+= "/";
    	}
    	name = name.substring(3) + "." + getFileExt(file.getFileName());
    	filePath += dir;
    	File path = new File(filePath);
    	if (!path.exists()) {
    		path.mkdirs();
    	}
        try {
            InputStream stream = file.getInputStream();
            OutputStream bos = new FileOutputStream(filePath + name);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.close();

            stream.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }

        file.destroy();

		return dir + name;
    }
    //2012-09-12 yl 上传网络图片
    /**
     * 从远程指定路径下载单张图片，存储到指定位置，图片名称随意取名
     * @param remotePath        String    :  远程图片路径，包含图片名称
     * @param savePath          String    :  保存图片路径，不包含图片名称
     * @param saveImageName     String    :  保存图片名称
     * @return                  boolean   ： true：图片下载成功 false：图片下载失败
     */
    private static boolean downloadNetImage(String remotePath, String savePath, String saveImageName) {
        // 定义操作标识
        boolean flag = false;
        
        try {
            // 获取远程图片地址
            URL url = new URL(remotePath);
            // 判断存储路径是否存在,不存在创建
            File realSavePath = new File(savePath);
            if(!realSavePath.exists()) {
                realSavePath.mkdir();
            }
            // 下载的图片保存的地址
            File outFile = new File(realSavePath + "/" + saveImageName);
            // 输出流
            OutputStream out = new FileOutputStream(outFile);
            // 输入流
            InputStream input = url.openStream();
            // 字节数组，获取远程图片信息
            byte[] buff = new byte[1024];
            while(true) {
                int readIndex = input.read(buff);
                // 读到最后，退出
                if(readIndex == -1) {
                    break;
                }
                byte[] temp = new byte[readIndex];
                // copy远程图片信息到临时字节数组中
                System.arraycopy(buff, 0, temp, 0, readIndex);
                // 形成新图片
                out.write(temp);
            }
            input.close();
            out.close();
            // 设置操作标识为true
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } 
        
        // 返回操作结果
        return flag;
    }
    public static String uploadNetFile(String fileurl, String savePath) {
    	if(fileurl==null||fileurl.length()<1){return null;}
    	String name = String.valueOf(System.currentTimeMillis());
    	String dir = name.substring(0, 3) + "/";
    	name = name.substring(3) + "." + getFileExt(fileurl);
    	if(!savePath.endsWith("/")){
    		savePath+= "/";
    	}
    	savePath += dir;
    	File path = new File(savePath);
    	if (!path.exists()) {
    		path.mkdirs();
    	}
		try {
			if(downloadNetImage(fileurl, savePath,name)){
				return dir + name;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
    }
   public static String uploadNetFileBig(String dir,String fileurl, String filePath,String smallNamePre,String smallPicExt){
	   if(filePath==null) return null;
   	if(!filePath.endsWith("/")){
   		filePath+= "/";
   	}
   	String name = smallNamePre + "_b." + smallPicExt;
   	if(StringUtil.isNull(dir)){
   		dir = "";
   	}else{
   		dir+= "/";
   	}
   	
   	filePath += dir;
   	
   	File path = new File(filePath);
   	if (!path.exists()) {
   		path.mkdirs();
   	}
   	try {
		if(downloadNetImage(fileurl, filePath,name)){
			return dir + name;
		}
	} catch (Exception e) {
		e.printStackTrace();
	} 
	return null;
   }
   public static boolean uploadNetFile(String fileurl, String savePath, String name) {
   	
		try {
			if(downloadNetImage(fileurl, savePath,name)){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
   }
   
   public static boolean deleteImage( String path) {
	   System.out.println("[STAFFPHOTOLOG" + DateUtil.getNow() + "]" + "2去寻找路径下"+path );
	   boolean result = false;
	   File image = new File(path);
	   System.out.println("[STAFFPHOTOLOG" + DateUtil.getNow() + "]" + "2文件对象建立"+path );
	   if( image.exists() ) {
		   System.out.println("[STAFFPHOTOLOG" + DateUtil.getNow() + "]" + "2文件存在"+path );
		  result = image.delete();
		  System.out.println("[STAFFPHOTOLOG" + DateUtil.getNow() + "]" + "2调用删除"+path );
	   } else {
		   System.out.println("[STAFFPHOTOLOG" + DateUtil.getNow() + "]" + "2文件不存在修改result" );
		   result = true;
	   }
	   System.out.println("[STAFFPHOTOLOG" + DateUtil.getNow() + "]" + "2返回值" );
	   return  result;
   }
   
   
   public static boolean writeFile(String path, String name, String content){
	   boolean result = true;
	   
	   File file = new File(path+"\\"+name);
	   FileOutputStream out = null;
	   content = StringUtil.convertNull(content);
	   try{
		   if(!file.exists()){
			   file.createNewFile();
		   }
		   out = new FileOutputStream(file);
		   out.write(content.getBytes("UTF-8"));
		   out.close();
	   }catch(Exception e){
		   e.printStackTrace();
		   result = false;
	   }
	   
	   return result;
   }
 }
