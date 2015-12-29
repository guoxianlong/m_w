package cn.mmb.order.domain.exception;

public class PopBuyPlanException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**内部异常码*/
	private String localCode;
	
	/**其他平台返回的异常码*/
	private String resultCode;
	
	
	public PopBuyPlanException(String localCode){
		this.localCode = localCode;
	}
	
	public PopBuyPlanException(String localCode, String resultCode){
		this.localCode = localCode;
		this.resultCode = resultCode;
	}

	public String getLocalCode() {
		return localCode;
	}

	public String getResultMessage() {
		String resultMessage = ExceptionCode.localCodeMsgMap.get(this.localCode);
		if(this.resultCode != null){
			resultMessage = this.resultCode + ":" + resultMessage;
		}
		return resultMessage;
	}

	@Override
	public String getMessage() {
		return this.localCode + ":" + this.getResultMessage();
	}
}
