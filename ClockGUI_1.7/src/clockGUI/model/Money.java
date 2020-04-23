package clockGUI;

public class Money 
{
	String costType = null;
	int price = 0;
	public Money(String costType, int price)
	{
		this.costType=costType;
		this.price=price;
	}
	
	public String getCostType()
	{
		return costType;
	}
	
	public int getPrice()
	{
		return price;
	}
}
