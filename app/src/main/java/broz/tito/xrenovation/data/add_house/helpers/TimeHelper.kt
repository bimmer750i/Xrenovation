package broz.tito.xrenovation.data.add_house.helpers

class TimeHelper {

    companion object {
        fun getUtcTime() : Long {
            return System.currentTimeMillis()
        }
    }

}