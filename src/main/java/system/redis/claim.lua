function testClaim()
    local stock_num=tonumber("29")
    local quota=tonumber("1")
    if stock_num > 0 then
        if quota > 0 then
            --redis.call('decr',KEYS[1])
            --redis.call('decr',KEYS[2])
            return 0
        else
            return 1
        end
    else
        return 2
    end
end


print(testClaim())

--local stock_num=redis.call('get',KEYS[1])
--local quota=redis.call('get',KEYS[2])
--if stock_num > tonumber(ARGV[1]) then
--    if quota > tonumber(ARGV[2]) then
--        redis.call('decr',KEYS[1])
--        redis.call('decr',KEYS[2])
--        return 0
--    else
--        return 1
--    end
--else
--    return 2
--end