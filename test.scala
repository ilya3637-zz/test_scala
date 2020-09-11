import scala.collection.mutable.ArrayBuffer
import scalaj.http.{Http, HttpResponse}
import org.scalacheck.Properties
import org.scalacheck._
import org.scalatest._
import org.scalatest.prop._

object test {
	def binance_f (bitcoin_count: Int): Double = {
		val response1: HttpResponse[String] = Http("https://api.binance.com/api/v1/depth?symbol=BTCUSDT&limit=1000").asString
        var binance_string: String = response1.body
        binance_string = binance_string.substring(binance_string.indexOf("asks") + 8,binance_string.length() -2)
		var i = 0
        var min = 999999.0

        while (i<binance_string.length()){
        	var temp_string = binance_string.substring(i,binance_string.indexOf("]",i))
        	if (temp_string.substring(temp_string.indexOf(",") + 2, temp_string.lastIndexOf(".")).toInt >= bitcoin_count){
        		if ((temp_string.substring(1, temp_string.indexOf(",") - 1).toDouble) < min) {
        			min = temp_string.substring(1, temp_string.indexOf(",") - 1).toDouble        			
        		}
        	}
        	i += temp_string.length() + 3        	
        }
        return(min)
	}

	def huobi_f (bitcoin_count: Int): Double = {
		val response2: HttpResponse[String] = Http("https://api.huobi.pro/market/depth?symbol=btcusdt&type=step0").asString
        var huobi_string: String = response2.body
        huobi_string = huobi_string.substring(huobi_string.indexOf("asks") + 7,huobi_string.indexOf("version") - 3)   
        var i = 0
        var min = 999999.0

        i = 0
        while (i<huobi_string.length()){
        	var temp_string = huobi_string.substring(i,huobi_string.indexOf("]",i))
        	if (temp_string.substring(temp_string.indexOf(",") + 1, temp_string.lastIndexOf(".")).toInt >= bitcoin_count){
        		if ((temp_string.substring(1, temp_string.indexOf(",")).toDouble) < min) {
        			min = temp_string.substring(1, temp_string.indexOf(",")).toDouble 
        		}
        	}
        	i += temp_string.length() + 2        	
        }
        return(min)
	}

    def main(args: Array[String]) = {
    	val bitcoin_count = 10



		val smallInteger = Gen.choose(0,100)
		val test_function = Prop.forAll(smallInteger) { n => binance_f(n) >= 0}

		
		test_function.check
		val min_binance = binance_f(bitcoin_count)
		val min_huobi = huobi_f(bitcoin_count)

        if (min_binance < min_huobi) {
        	println("binance better, need " + min_binance*bitcoin_count + "$")
        } else {
			println("huobi better, need " + min_huobi*bitcoin_count + "$")
        }

    }
}
