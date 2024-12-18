package com.linecorp.planetkit.demo

import com.linecorp.planetkit.demo.uikit.UiKitApplication

class DemoApplication: UiKitApplication(
    ServiceConstants.PLANET_CLOUD_URL,
    ServiceConstants.APP_SERVER_URL,
    ServiceConstants.SERVICE_ID,
    ServiceConstants.REGION,
    ServiceConstants.API_KEY,
    R.raw.planet_end_48k,
    R.raw.planet_hold_48k,
    R.raw.planet_ringback_48k,
    R.raw.planet_ring_48k,
)